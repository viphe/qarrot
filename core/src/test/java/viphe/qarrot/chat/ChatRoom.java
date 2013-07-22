package viphe.qarrot.chat;


import viphe.qarrot.*;

@Receives(MediaTypes.TEXT_PLAIN)
@Sends(MediaTypes.TEXT_PLAIN)
public class ChatRoom {

    @RouteIn("chat_room")
    @RouteOut("chat_room")
    public String handleLine(Qarrot qarrot, String line) {
        System.out.println("on server: " + line + " with Qarrot@" + qarrot.hashCode());
        return line;
    }
}
