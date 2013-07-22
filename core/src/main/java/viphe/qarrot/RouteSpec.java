package viphe.qarrot;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public interface RouteSpec {

    Route declareOn(Channel channel, RouteSpecMap routeSpecMap) throws IOException;
}
