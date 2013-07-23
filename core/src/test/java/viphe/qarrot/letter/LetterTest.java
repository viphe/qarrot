package viphe.qarrot.letter;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;
import viphe.qarrot.Qarrot;
import viphe.qarrot.Routes;

import static org.junit.Assert.*;

public class LetterTest {

    public static class ServerModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(SantaClausMailbox.class).in(Singleton.class);
        }
    }

    @Test(timeout = 5000)
    public void test() throws Exception {
        Qarrot server = new Qarrot(new ConnectionFactory());
        server.configure(new ServerModule());

        Qarrot kidPostman = new Qarrot(new ConnectionFactory());
        String wish = "I want a plane!";
        kidPostman.send(Routes.queue("santa_claus"), wish);

        SantaClausMailbox santaClausMailbox = server.getInjector().getInstance(SantaClausMailbox.class);
        while (santaClausMailbox.letters.isEmpty()) {
            Thread.sleep(100);
        }

        assertEquals(1, santaClausMailbox.letters.size());
        assertEquals(wish, santaClausMailbox.letters.get(0));
    }

}
