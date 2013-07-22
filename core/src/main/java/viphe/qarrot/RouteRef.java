package viphe.qarrot;

import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/21/13
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class RouteRef implements RouteSpec {

    private final String name;

    public RouteRef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "&" + name;
    }

    @Override
    public Route declareOn(Channel channel, RouteSpecMap routeSpecMap) throws IOException {
        return routeSpecMap.getRouteSpec(name).declareOn(channel, routeSpecMap);
    }
}
