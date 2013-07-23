package viphe.qarrot.letter;

import viphe.qarrot.RouteIn;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

public class SantaClausMailbox {

    public List<String> letters = new ArrayList<String>();

    @RouteIn("santa_claus")
    @Consumes(MediaType.TEXT_PLAIN)
    public void receiveLetter(String letter) {
        letters.add(letter);
    }
}
