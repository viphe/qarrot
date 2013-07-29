package viphe.qarrot.beer;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;
import viphe.qarrot.Qarrot;

import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

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
        thirstyClient.configure(null);

        Beer beer = thirstyClient.call("bar", 400000, MediaType.TEXT_PLAIN_TYPE, "belgian", Beer.class);
        assertEquals("belgian", beer.type);
        assertNotNull(beer.replyTo);
        assertEquals(1, beerServer.ordered.size());
        assertEquals("belgian", beerServer.ordered.get(0));

        beer = thirstyClient.call("bar", 400000, MediaType.TEXT_PLAIN_TYPE, "cheapest", Beer.class);
        assertNull(beer);

        try {
            thirstyClient.call("bar", 400000, MediaType.TEXT_PLAIN_TYPE, "martian", Beer.class);
            fail("was expecting an error");
        } catch (IOException e) {
            // expected
            assertTrue(e.getMessage().contains("martian"));
        }
    }

}
