package viphe.qarrot;

import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;
import com.google.inject.Key;
import com.google.common.reflect.Invokable;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/20/13
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class EndPointMethod {

    private final RouteSpec routeIn;
    private final Key<?> bindingKey;
    private final Method method;
    private final TypeToken typeToken;
    private final Invokable invokable;
    private final Consumes receives;
    private final MediaType receivedMediaType;
    private final MediaType sentMediaType;
    private final ParamProvider[] paramProviders;


    public EndPointMethod(RouteSpec routeIn, Key<?> bindingKey, Class clazz, Method method) {
        this.routeIn = routeIn;
        this.bindingKey = bindingKey;
        this.method = method;
        this.typeToken = TypeToken.of(clazz);
        this.invokable = typeToken.method(method);

        Consumes receives = invokable.getAnnotation(Consumes.class);
        this.receives = receives == null ? (Consumes) typeToken.getRawType().getAnnotation(Consumes.class) : receives;

        this.receivedMediaType = this.receives == null ? MediaType.TEXT_PLAIN_TYPE : MediaType.valueOf(this.receives.value()[0]);

        Produces sends = invokable.getAnnotation(Produces.class);
        if (sends == null) sends = (Produces) clazz.getAnnotation(Produces.class);
        this.sentMediaType = sends == null ? MediaType.TEXT_PLAIN_TYPE : MediaType.valueOf(sends.value()[0]);

        this.paramProviders = createParamProviders(this.invokable, receivedMediaType);
    }

    private static ParamProvider[] createParamProviders(Invokable invokable, MediaType receivedMediaType) {
        List<Parameter> parameters = invokable.getParameters();
        ParamProvider[] paramProviders = new ParamProvider[parameters.size()];

        for (int i = parameters.size(); --i >= 0;) {
            Parameter parameter = parameters.get(i);
            AmqpProp amqpProp = parameter.getAnnotation(AmqpProp.class);
            if (amqpProp != null) {
                paramProviders[i] = new AmqpPropProvider((Class<?>) parameter.getType().getType(), amqpProp.value());

            } else if (parameter.getType().isAssignableFrom(Qarrot.class)) {
                paramProviders[i] = new QarrotParamProvider();

            } else if (parameter.getType().getType() instanceof Class) {
                paramProviders[i] = new BodyParamProvider((Class<?>) parameter.getType().getType(), receivedMediaType);

            } else {
                throw new RuntimeException("unsupported parameter " + parameter);
            }
        }

        return paramProviders;
    }

    public RouteSpec getRouteIn() {
        return routeIn;
    }

    public Object call(Event event)
    throws IOException {
        Object actionObject = event.getInjector().getProvider(bindingKey).get();
        try {
            Object result;
            MediaType resultMediaType = sentMediaType;
            boolean isError;
            try {
                result = method.invoke(actionObject, (Object[]) prepareArguments(event));
                isError = false;
            } catch (Throwable t) {
                isError = true;
                Class returnType = method.getReturnType();
                if (Void.TYPE == returnType) {
                    result = new RpcError(t.getMessage(), t);
                    resultMediaType = MediaTypes.asError(resultMediaType);
                } else {
                    try {
                        result = ConstructorUtils.invokeExactConstructor(
                            returnType, new Object[] { t.getMessage(), t }, new Class[] { String.class, Throwable.class });
                    } catch (NoSuchMethodException e) {
                        result = new RpcError(t.getMessage(), t);
                        resultMediaType = MediaTypes.asError(resultMediaType);
                    } catch (InstantiationException e) {
                        result = new RpcError(t.getMessage(), t);
                        resultMediaType = MediaTypes.asError(resultMediaType);
                    }
                }
            }

            // RPC call
            String replyTo = event.getProperties().getReplyTo();
            if (replyTo != null) {
                event.getQarrot().send(
                    Routes.queue(replyTo),
                    AmqpUtils.propertiesBuilder(resultMediaType)
                        .correlationId(event.getProperties().getCorrelationId())
                        .build(),
                    result);
            }

            event.getChannel().basicAck(event.getEnvelope().getDeliveryTag(), false);
            return result;

        } catch (IllegalAccessException e) {
            throw new IOException(e);
        } catch (InvocationTargetException e) {
            throw new IOException(e);
        }
    }

    private Object[] prepareArguments(Event event) throws IOException {
        Object[] args = new Object[paramProviders.length];

        for (int i = args.length; --i >= 0;) {
            args[i] = paramProviders[i].get(event);
        }

        return args;
    }
}
