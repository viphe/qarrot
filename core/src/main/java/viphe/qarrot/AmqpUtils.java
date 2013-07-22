package viphe.qarrot;

import com.rabbitmq.client.AMQP.BasicProperties;

import javax.ws.rs.core.MediaType;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/21/13
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class AmqpUtils {

    public static BasicProperties.Builder propertiesBuilder(MediaType mediaType) {
        BasicProperties.Builder bob = new BasicProperties.Builder();
        if (mediaType != null) {
            bob = bob.contentType(mediaType.toString());
        }
        return bob;
    }
}
