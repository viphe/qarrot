package viphe.qarrot.chat;

import java.util.Set;
import java.util.TreeSet;

public class Room {

    public final String name;
    public final String exchange;
    public final Set<String> pseudos = new TreeSet<String>();

    public Room(String name, String exchange) {
        this.name = name;
        this.exchange = exchange;
    }

    public void add(String pseudo) {
        pseudos.add(pseudo);
    }
}
