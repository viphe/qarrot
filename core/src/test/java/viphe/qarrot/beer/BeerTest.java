package viphe.qarrot.beer;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;
import viphe.qarrot.Qarrot;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;

public class BeerTest {

    public static class ServerModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(BeerServer.class).in(Singleton.class);
        }
    }

    @Test(timeout = 50000)
    public void test() throws Exception {
        Qarrot server = new Qarrot(new ConnectionFactory());
        server.configure(new ServerModule());
        BeerServer beerServer = server.getInjector().getInstance(BeerServer.class);

        Qarrot thirstyClient = new Qarrot(new ConnectionFactory());
        Beer beer = thirstyClient.call("bar", 400000, MediaType.TEXT_PLAIN_TYPE, "belgian", Beer.class);

        assertEquals("belgian", beer.type);
        assertEquals(1, beerServer.ordered.size());
        assertEquals("belgian", beerServer.ordered.get(0));
    }

}
