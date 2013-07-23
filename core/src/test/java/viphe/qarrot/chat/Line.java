package viphe.qarrot.chat;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/22/13
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class Line {

    public String pseudo;
    public String room;
    public String text;


    public Line() {
    }

    public Line(String pseudo, String room, String text) {
        this.pseudo = pseudo;
        this.room = room;
        this.text = text;
    }
}
