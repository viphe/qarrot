package viphe.qarrot.chat;

import com.google.inject.Inject;
import viphe.qarrot.Qarrot;
import viphe.qarrot.Routes;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Consumes(MediaType.APPLICATION_JSON)
public class ChatServer {

    private final Qarrot qarrot;
    private Map<String, Room> rooms = new HashMap<String, Room>();


    @Inject
    public ChatServer(Qarrot qarrot) throws IOException {
        this.qarrot = qarrot;
        addRoom("lounge");
    }

    public void addRoom(String name) throws IOException {
        Room room = new Room(name, UUID.randomUUID().toString());
        qarrot.declare(Routes.exchange(room.exchange));
        rooms.put(room.name, room);
    }

    public Room getRoom(String name) {
        return rooms.get(name);
    }

    @Path("chat_room")
    public ChatRoom room(@PathParam("name") String roomName) {
        Room room = rooms.get(roomName);
        return new ChatRoom(room);
    }

    @Path("chat_command")
    public Room enter(Qarrot qarrot, EnterRoom command) throws IOException {
        Room room = getRoom(command.room);
        room.add(command.pseudo);
        room(room.name).line(qarrot, new Line(command.pseudo, room.name, command.pseudo + " entered"));
        return room;
    }
}
