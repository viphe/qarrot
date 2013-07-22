package viphe.qarrot;

import com.google.inject.Injector;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/21/13
 * Time: 9:02 AM
 * To change this template use File | Settings | File Templates.
 */
public final class Event {

    private final Qarrot qarrot;
    private final Channel channel;
    private final Envelope envelope;
    private final AMQP.BasicProperties properties;
    private final byte[] body;


    public Event(Qarrot qarrot, Channel channel, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        this.qarrot = qarrot;
        this.channel = channel;
        this.envelope = envelope;
        this.properties = properties;
        this.body = body;
    }

    public Qarrot getQarrot() {
        return qarrot;
    }

    public Injector getInjector() {
        return qarrot.getInjector();
    }

    public Channel getChannel() {
        return channel;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public AMQP.BasicProperties getProperties() {
        return properties;
    }

    public byte[] getBody() {
        return body;
    }
}
