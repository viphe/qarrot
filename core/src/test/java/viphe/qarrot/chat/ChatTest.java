package viphe.qarrot.chat;

import com.google.inject.AbstractModule;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;
import viphe.qarrot.Qarrot;
import viphe.qarrot.Routes;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/20/13
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChatTest {

    public static class ServerModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ChatRoom.class);
        }
    }

    public static class ClientModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ChatClient.class);
        }
    }

    @Test
    public void test() throws Exception {
        Qarrot server = new Qarrot(new ConnectionFactory());
        server.configure(new ServerModule());

        Qarrot client1 = new Qarrot(new ConnectionFactory());
        client1.addRouteSpec("server", Routes.parse("chat_room<<", true));
        client1.configure(new ClientModule());

        Qarrot client2 = new Qarrot(new ConnectionFactory());
        client2.addRouteSpec("server", Routes.parse("chat_room<<", true));
        client2.configure(new ClientModule());

        ChatClient chatClient = client1.getInjector().getInstance(ChatClient.class);

        for (int i = 1; i <= 1; i++) {
            chatClient.sendLine("coucou les amis ! (" + i + ")");
            Thread.sleep(5000);
        }
    }
}
