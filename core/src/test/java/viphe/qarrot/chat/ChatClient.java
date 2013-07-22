package viphe.qarrot.chat;

import com.google.inject.Inject;
import viphe.qarrot.*;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Sends(MediaType.TEXT_PLAIN)
public class ChatClient {

    private Qarrot qarrot;


    @Inject
    public ChatClient(Qarrot qarrot) {
        if (qarrot == null) throw new NullPointerException("'qarrot' cannot be null");

        this.qarrot = qarrot;
    }

    public void sendLine(String line) throws IOException {
        qarrot.send(Routes.queue("chat_room"), line);
    }

    @RouteIn("&server")
    public void receiveLine(String line) {
        System.out.println("on client " + hashCode() + ": " + line);
    }
}
