package viphe.qarrot.chat;

import viphe.qarrot.Qarrot;
import viphe.qarrot.Routes;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
public class ChatServer {

    public Room getRoom(String name) {
        throw new RuntimeException("not implemented");
    }

    public Line enter(Qarrot qarrot, EnterRoom command) {
        Room room = getRoom(command.room);
        room.add(command.pseudo);
        return new Line(command.pseudo, room.name, command.pseudo + " entered");
    }
}
