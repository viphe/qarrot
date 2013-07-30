package viphe.qarrot;

import com.google.inject.*;
import com.google.inject.util.Modules;
import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Qarrot implements Closeable, RouteSpecMap {

    private static final Logger log = LoggerFactory.getLogger(Qarrot.class);

    private static final MultivaluedMap<String, String> EMPTY_HEADERS = MultivaluedMaps.empty();
    private static final MultivaluedMap<String, Object> EMPTY_OBJECT_HEADERS = MultivaluedMaps.empty();
    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel channel;

    private Module injectionConfig;
    private Injector globalInjector;

    private List<Action> actions = new ArrayList<Action>();
    private Map<String, RouteSpec> namedRouteSpecs = new HashMap<String, RouteSpec>();
    private List<MessageBodyReader> messageBodyReaders = new ArrayList<MessageBodyReader>();
    private List<MessageBodyWriter> messageBodyWriters = new ArrayList<MessageBodyWriter>();


    public Qarrot(ConnectionFactory connectionFactory) throws IOException {
        if (connectionFactory == null) throw new NullPointerException("'connectionFactory' cannot be null");

        this.connectionFactory = connectionFactory;
        this.connection = connectionFactory.newConnection();
        this.channel = connection.createChannel();

        messageBodyReaders.add(ByteArrayConverter.INSTANCE);
        messageBodyWriters.add(ByteArrayConverter.INSTANCE);
        messageBodyReaders.add(StringConverter.INSTANCE);
        messageBodyWriters.add(StringConverter.INSTANCE);
    }

    @Override
    public void close() throws IOException {
        channel.close();
        connection.close();
    }

    public void addRouteSpec(String name, RouteSpec routeSpec) {
        namedRouteSpecs.put(name, routeSpec);
    }

    @Override
    public RouteSpec getRouteSpec(String name) {
        return namedRouteSpecs.get(name);
    }

    private class QarrotModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Qarrot.class).toInstance(Qarrot.this);
            bind(ObjectMapper.class);
        }
    }

    public Injector getInjector() {
        if (globalInjector == null) {
            throw new IllegalStateException();
        }
        return globalInjector;
    }

    public void configure(Module module) throws IOException {
        Module totalModule = module == null ? new QarrotModule() : Modules.override(new QarrotModule()).with(module);
        globalInjector = Guice.createInjector(totalModule);

        scanBindings();

        JacksonJsonProvider jacksonJsonProvider = globalInjector.getInstance(JacksonJsonProvider.class);
        NullableConverter nullableJsonConverter = new NullableConverter(jacksonJsonProvider, jacksonJsonProvider);
        messageBodyReaders.add(nullableJsonConverter);
        messageBodyWriters.add(nullableJsonConverter);

        declareConsumers();
    }

    private void scanBindings() throws IOException {
        for (Map.Entry<Key<?>, Binding<?>> e : globalInjector.getAllBindings().entrySet()) {
            Key<?> bindingKey = e.getKey();
            Binding<?> binding = e.getValue();
            TypeLiteral boundTypeLiteral = bindingKey.getTypeLiteral();
            Type boundType = boundTypeLiteral.getType();
            if (boundType instanceof Class) {
                final Class boundClass = (Class) boundType;
                for (Method method : boundClass.getMethods()) {
                    if ((method.getModifiers() & Modifier.STATIC) == 0) {
                        for (Annotation annotation : method.getAnnotations()) {
                            if (annotation instanceof RouteIn) {
                                RouteIn routeInSpec = (RouteIn) annotation;
                                RouteSpec routeIn = Routes.parse(routeInSpec.value(), true);
                                actions.add(new Action(routeIn, bindingKey, boundClass, method));
                            }
                        }
                    }
                }

                if (MessageBodyReader.class.isAssignableFrom(boundClass)) {
                    messageBodyReaders.add(0, (MessageBodyReader) globalInjector.getInstance(bindingKey));
                }
                if (MessageBodyWriter.class.isAssignableFrom(boundClass)) {
                    messageBodyWriters.add(0, (MessageBodyWriter) globalInjector.getInstance(bindingKey));
                }
            }
        }
    }

    private void declareConsumers() throws IOException{
        for (final Action action : actions) {
            final RouteSpec routeSpec = action.getRouteIn();
            Route route = routeSpec.declareOn(channel, this);

            log.debug("consuming on " + route.getQueue() + " ==> " + action);

            boolean autoAck = false;
            channel.basicConsume(route.getQueue(), autoAck, action.toString(),
                    new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag,
                                                   Envelope envelope,
                                                   AMQP.BasicProperties properties,
                                                   byte[] body)
                                throws IOException
                        {
                            String routingKey = envelope.getRoutingKey();
                            String contentType = properties.getContentType();
                            long deliveryTag = envelope.getDeliveryTag();

                            action.call(new Event(Qarrot.this, channel, envelope, properties, body));
                        }
                    });
        }
    }

    public MessageBodyReader findReader(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        for (MessageBodyReader reader : messageBodyReaders) {
            if (reader.isReadable(type, genericType, annotations, mediaType)) {
                return reader;
            }
        }
        return null;
    }

    public <T> T fromBytes(MediaType mediaType, byte[] bytes, Class<T> clazz) throws IOException {
        MessageBodyReader<T> reader = findReader(clazz, null, EMPTY_ANNOTATIONS, mediaType);
        if (reader == null) {
            throw new IllegalArgumentException("cannot convert bytes (" + mediaType + ") to " + clazz);
        }
        return reader.readFrom(clazz, clazz, EMPTY_ANNOTATIONS, mediaType, EMPTY_HEADERS, new ByteArrayInputStream(bytes));
    }

    public MessageBodyWriter findWriter(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        for (MessageBodyWriter writer : messageBodyWriters) {
            if (writer.isWriteable(type, genericType, annotations, mediaType)) {
                return writer;
            }
        }
        return null;
    }
    
    public <T> byte[] toBytes(MediaType mediaType, T o) throws IOException {
        if (o == null) {
            return null;
        }

        Class<T> clazz = (Class<T>) o.getClass();
        MessageBodyWriter<T> writer = findWriter(clazz, null, EMPTY_ANNOTATIONS, mediaType);
        if (writer == null) {
            throw new IllegalArgumentException("cannot convert " + clazz.getName() + " into byte[] (" + mediaType + ")");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        writer.writeTo(o, clazz, clazz, EMPTY_ANNOTATIONS, mediaType, EMPTY_OBJECT_HEADERS, baos);
        return baos.toByteArray();
    }

    public void send(RouteSpec routeSpec, Object payload) throws IOException {
        send(routeSpec, "", (AMQP.BasicProperties) null, payload);
    }

    public void send(RouteSpec routeSpec, MediaType mediaType, Object payload) throws IOException {
        send(routeSpec, "", mediaType, payload);
    }

    public void send(RouteSpec routeSpec, AMQP.BasicProperties properties, Object payload) throws IOException {
        send(
            routeSpec,
            "",
            properties,
            getBytes(MediaTypes.withCharset(properties.getContentType(), properties.getContentEncoding()), payload));
    }

    public void send(RouteSpec routeSpec, String routingKey, MediaType mediaType, Object payload) throws IOException {
        send(routeSpec, routingKey, AmqpUtils.propertiesBuilder(mediaType).build(), payload);
    }

    public void send(RouteSpec routeSpec, String routingKey, AMQP.BasicProperties properties, Object payload) throws IOException {
        Route route = routeSpec.declareOn(channel, this);
        route.publishOn(channel, routingKey, properties, toBytes(MediaTypes.valueOf(properties), payload));
    }

    public RpcResponse call(String queue, long timeout, BasicProperties properties, byte[] body)
    throws IOException, TimeoutException, InterruptedException {
        Rpc rpc = new Rpc(channel, queue, timeout);
        return rpc.call(properties, body);
    }

    public <T> T call(String queue, long timeout, MediaType mediaType, Object payload, Class<T> responseClass)
    throws IOException, TimeoutException, InterruptedException {
        String encoding = MediaTypes.getCharset(mediaType);

        byte[] body = getBytes(mediaType, payload);

        AMQP.BasicProperties.Builder bpb = new AMQP.BasicProperties.Builder();
        if (encoding != null) {
            bpb.contentEncoding(encoding);
        }

        RpcResponse response = call(queue, timeout, bpb.build(), body);

        MediaType responseMediaType = MediaType.valueOf(response.getProperties().getContentType());
        if (MediaTypes.isError(responseMediaType)) {
            RpcError rpcError;
            try {
                rpcError = fromBytes(responseMediaType, response.getBody(), RpcError.class);
            } catch (IllegalArgumentException e) {
                throw new IOException("unknown remote error");
            }

            if (rpcError == null) {
                throw new IOException("unknown remote error");
            }  else {
                throw new IOException(rpcError.error + "\n" + rpcError.trace);
            }

        } else {
            return fromBytes(responseMediaType, response.getBody(), responseClass);
        }
    }

    private byte[] getBytes(MediaType mediaType, Object payload) throws IOException {
        return toBytes(mediaType, payload);
    }
}
