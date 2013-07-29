package viphe.qarrot;

import com.rabbitmq.client.AMQP;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 */
public class AmqpPropProvider<T> extends ParamProvider<T> {

    private final String property;
    private final Method getter;


    public AmqpPropProvider(Class<T> resultType, String property) {
        super(resultType);
        this.property = property;
        try {
            getter = AMQP.BasicProperties.class.getMethod(
                "get" + Character.toUpperCase(property.charAt(0)) + property.substring(1));
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("unknown AMQP property: " + property);
        }
    }

    @Override
    public T get(Event event) throws IOException {
        try {
            return resultType.cast(getter.invoke(event.getProperties()));
        } catch (IllegalAccessException e) {
            throw new IOException(e);
        } catch (InvocationTargetException e) {
            throw new IOException(e);
        }
    }
}
