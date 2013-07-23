package viphe.qarrot;

import static org.junit.Assert.*;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

/**
 *
 */
public class MediaTypeHeaderDelegateTest {

    @Test
    public void fromString() throws Exception {
        MediaTypeHeaderDelegate d = new MediaTypeHeaderDelegate();

        MediaType mediaType = d.fromString("text/plain");
        assertEquals("text", mediaType.getType());
        assertEquals("plain", mediaType.getSubtype());
        assertTrue(mediaType.getParameters().isEmpty());

        mediaType = d.fromString("text/plain;charset=US-ASCII");
        assertEquals("text", mediaType.getType());
        assertEquals("plain", mediaType.getSubtype());
        assertEquals(1, mediaType.getParameters().size());
        assertEquals("US-ASCII", mediaType.getParameters().get("charset"));

        mediaType = d.fromString("text/plain;charset=US-ASCII&life=good");
        assertEquals("text", mediaType.getType());
        assertEquals("plain", mediaType.getSubtype());
        assertEquals(2, mediaType.getParameters().size());
        assertEquals("US-ASCII", mediaType.getParameters().get("charset"));
        assertEquals("good", mediaType.getParameters().get("life"));
    }
}
