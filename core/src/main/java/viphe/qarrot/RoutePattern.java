package viphe.qarrot;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/20/13
 * Time: 11:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoutePattern implements RouteSpec {

    private static final Logger log = LoggerFactory.getLogger(Route.class);

    private final boolean inbound;
    private final String exchange;
    private final String bindingKey;
    private final String queue;


    public RoutePattern(boolean inbound, String exchange, String bindingKey, String queue) {
        this.inbound = inbound;
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

    public Route declareOn(Channel channel, RouteSpecMap routeSpecMap) throws IOException {
        if (isQueue()) {
            log.debug("declaring queue " + getQueue());
            channel.queueDeclare(getQueue(), false, false, false, null);

        } else if (isExchange()) {
            log.debug("declaring exchange " + getExchange());
            channel.exchangeDeclare(getExchange(), "fanout", false);
            if (inbound) {
                String exclusiveQueue = channel.queueDeclare().getQueue();
                channel.queueBind(exclusiveQueue, getExchange(), ""); // assuming fanout for now
                return new Route(exchange, bindingKey, exclusiveQueue);
            }

        } else {
            throw new RuntimeException("unsupported declaration");
        }

        return new Route(exchange, bindingKey, queue);
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
}
