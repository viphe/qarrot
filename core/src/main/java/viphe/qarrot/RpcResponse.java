package viphe.qarrot;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/22/13
 * Time: 12:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class RpcResponse {

    private final Envelope envelope;
    private final AMQP.BasicProperties properties;
    private final byte[] body;


    public RpcResponse(Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        this.envelope = envelope;
        this.properties = properties;
        this.body = body;
    }

    public RpcResponse(QueueingConsumer.Delivery delivery) {
        this(delivery.getEnvelope(), delivery.getProperties(), delivery.getBody());
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
