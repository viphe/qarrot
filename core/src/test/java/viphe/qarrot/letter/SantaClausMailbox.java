package viphe.qarrot.letter;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

public class SantaClausMailbox {

    public List<String> letters = new ArrayList<String>();

    @Path("santa_claus")
    @Consumes(MediaType.TEXT_PLAIN)
    public void receiveLetter(String letter) {
        letters.add(letter);
    }
}
