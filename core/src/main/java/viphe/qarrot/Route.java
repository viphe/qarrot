package viphe.qarrot;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * TODO RouteBuilder
 */
public class Route implements RouteSpec {

    private static final Logger log = LoggerFactory.getLogger(Route.class);

    private final String exchange;
    private final String bindingKey;
    private final String queue;


    public Route(String exchange, String bindingKey, String queue) {
        this.exchange = exchange;
        this.bindingKey = bindingKey;
        this.queue = queue;
    }

    public boolean isQueue() {
        return exchange == null;
    }

    public boolean isExchange() {
        return queue == null;
    }

    public String getExchange() {
        return exchange;
    }

    public String getBindingKey() {
        return bindingKey;
    }

    public String getQueue() {
        return queue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        if (bindingKey != null ? !bindingKey.equals(route.bindingKey) : route.bindingKey != null) return false;
        if (exchange != null ? !exchange.equals(route.exchange) : route.exchange != null) return false;
        if (queue != null ? !queue.equals(route.queue) : route.queue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = exchange != null ? exchange.hashCode() : 0;
        result = 31 * result + (bindingKey != null ? bindingKey.hashCode() : 0);
        result = 31 * result + (queue != null ? queue.hashCode() : 0);
        return result;
    }

    public String toString() {
        if (isQueue()) {
            return queue;
        } else if (isExchange()) {
            return exchange + "<<";
        } else {
            return exchange + "<<" + queue;
        }
    }

    @Override
    public Route declareOn(Channel channel, RouteSpecMap routeSpecMap) throws IOException {
        return this;
    }

    public void publishOn(Channel channel, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
        if (isQueue()) {
            channel.basicPublish("", queue, properties, body);

        } else {
            channel.basicPublish(exchange, routingKey, properties, body);
        }
    }
}
