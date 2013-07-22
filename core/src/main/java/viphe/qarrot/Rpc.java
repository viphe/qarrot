package viphe.qarrot;

import com.rabbitmq.client.AMQP.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Rpc {

    private final Channel channel;
    private final String requestQueueName;
    private final String replyQueueName;
    private final QueueingConsumer consumer;
    private long timeout;


    public Rpc(Channel channel, String queue, long timeout) throws IOException {
        this.channel = channel;
        this.requestQueueName = queue;
        this.replyQueueName = channel.queueDeclare().getQueue();
        this.consumer = new QueueingConsumer(channel);
        this.timeout = timeout;
        channel.basicConsume(replyQueueName, true, consumer);
    }

    public RpcResponse call(BasicProperties properties, byte[] request)
    throws IOException, TimeoutException, InterruptedException {
        String correlationId = java.util.UUID.randomUUID().toString();

        properties = (properties == null ? new BasicProperties.Builder() : properties.builder())
            .correlationId(correlationId)
            .replyTo(replyQueueName)
            .build();

        channel.basicPublish("", requestQueueName, properties,request);

        long limit = System.currentTimeMillis() + timeout;
        while (true) {
            QueueingConsumer.Delivery delivery;
            if (timeout < 0) {
                delivery = consumer.nextDelivery();
            } else {
                long loopTimeout = limit - System.currentTimeMillis();
                if (loopTimeout <= 0) {
                    throw new TimeoutException();
                }
                delivery = consumer.nextDelivery(loopTimeout);
            }

            if (delivery == null) throw new TimeoutException();

            if (delivery.getProperties().getCorrelationId().equals(correlationId)) {
                return new RpcResponse(delivery);
            }
        }
    }
}