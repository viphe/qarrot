package viphe.qarrot;

public class Routes {

    public static Route queue(String queue) {
        Route route = new Route(null, null, queue);
        return route;
    }

    public static RouteSpec parse(String routeSpec, boolean inbound) {
        if (routeSpec == null) throw new NullPointerException("'routeSpec' cannot be null");
        if (routeSpec.isEmpty()) throw new IllegalArgumentException("'routeSpec' cannot be empty");

        if (routeSpec.startsWith("&")) {
            return new RouteRef(routeSpec.substring(1));
        }

        String[] tokens = routeSpec.split("<<|=|/", -1);
        if (tokens.length == 1) {
            return inbound ? new RoutePattern(inbound, null, null, routeSpec) : new RoutePattern(inbound, routeSpec, null, null);
        } else {
            if (tokens.length == 2) {
                return new RoutePattern(inbound, tokens[0], null, tokens[1].isEmpty() ? null : tokens[1]);
            } else {
                throw new RuntimeException("not supported: " + routeSpec);
            }
        }
    }
}
