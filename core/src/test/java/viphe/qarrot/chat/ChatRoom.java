package viphe.qarrot.chat;


import viphe.qarrot.*;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Receives(MediaType.APPLICATION_JSON)
@Sends(MediaType.APPLICATION_JSON)
public class ChatRoom {

    private final Room room;


    public ChatRoom(Room room) {
        this.room = room;
    }

    @Path("#")
    public void line(Qarrot qarrot, Line line) throws IOException {
        System.out.println("[server] " + line.pseudo + " says \"" + line.text + "\"");
        qarrot.send(Routes.exchange(room.exchange), line);
    }
}
