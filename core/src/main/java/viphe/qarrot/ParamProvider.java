package viphe.qarrot;

import com.google.inject.Injector;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public interface ParamProvider {

    Object get(Event event) throws IOException;
}
