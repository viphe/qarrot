package viphe.qarrot.beer;

import viphe.qarrot.AmqpProp;
import viphe.qarrot.RouteIn;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BeerServer {

    public List<String> ordered = new ArrayList<String>();

    @RouteIn("bar")
    @Consumes(MediaType.TEXT_PLAIN)
    public Beer serveBeer(String beerType, @AmqpProp("replyTo") String replyTo) {
        ordered.add(beerType);

        if ("belgian".equals(beerType)) {
            return new Beer(beerType, replyTo);

        } else if ("cheapest".equals(beerType)) {
            return null;

        } else {
            throw new RuntimeException("no " + beerType + " beer around");
        }
    }
}
