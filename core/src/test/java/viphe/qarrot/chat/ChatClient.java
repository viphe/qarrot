package viphe.qarrot.chat;

import com.google.inject.Inject;
import viphe.qarrot.*;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Sends(MediaType.TEXT_PLAIN)
public class ChatClient {

    private Qarrot qarrot;


    @Inject
    public ChatClient(Qarrot qarrot) {
        if (qarrot == null) throw new NullPointerException("'qarrot' cannot be null");

        this.qarrot = qarrot;
    }

    public void enterRoom(String name) throws IOException, TimeoutException, InterruptedException {
        Room room =
            qarrot.call("chat_command", 5000, MediaType.APPLICATION_JSON_TYPE, new EnterRoom("lounge", "bob"), Room.class);
        qarrot.addRouteSpec("server", Routes.exchange(room.exchange));
    }

    public void sendLine(String line) throws IOException {
        qarrot.send(Routes.queue("chat"), line);
    }

    @Path("&server")
    public void receiveLine(String line) {
        System.out.println("on client " + hashCode() + ": " + line);
    }
}
