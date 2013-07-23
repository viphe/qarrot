package viphe.qarrot.chat;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/22/13
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class Room {
    public String name;
    public Set<String> pseudos = new TreeSet<String>();

    public void add(String pseudo) {
        pseudos.add(pseudo);
    }
}
