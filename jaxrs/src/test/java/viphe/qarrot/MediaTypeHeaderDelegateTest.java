package viphe.qarrot;

import static org.junit.Assert.*;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.Collections;

public class MediaTypeHeaderDelegateTest {

  @Test
  public void fromString() {
    MediaTypeHeaderDelegate mediaTypeHeaderDelegate = new MediaTypeHeaderDelegate();
    MediaType mediaType;

    mediaType = mediaTypeHeaderDelegate.fromString("text/plain");
    assertEquals("text", mediaType.getType());
    assertEquals("plain", mediaType.getSubtype());
    assertTrue(mediaType.getParameters().isEmpty());

    mediaType = mediaTypeHeaderDelegate.fromString("application/beer+json");
    assertEquals("application", mediaType.getType());
    assertEquals("beer+json", mediaType.getSubtype());
    assertTrue(mediaType.getParameters().isEmpty());

    mediaType = mediaTypeHeaderDelegate.fromString("application/json;charset=utf-8");
    assertEquals("application", mediaType.getType());
    assertEquals("json", mediaType.getSubtype());
    assertEquals("utf-8", mediaType.getParameters().get("charset"));

    mediaType = mediaTypeHeaderDelegate.fromString("application/json;charset=utf-8;odysseus");
    assertEquals("application", mediaType.getType());
    assertEquals("json", mediaType.getSubtype());
    assertEquals("utf-8", mediaType.getParameters().get("charset"));
    assertTrue(mediaType.getParameters().containsKey("odysseus"));
  }

  @Test
  public void testToString() {
    MediaTypeHeaderDelegate mediaTypeHeaderDelegate = new MediaTypeHeaderDelegate();

    MediaType mediaType = new MediaType("text", "plain");
    assertEquals("text/plain", mediaTypeHeaderDelegate.toString(mediaType));

    mediaType = new MediaType("text", "beer+json");
    assertEquals("text/beer+json", mediaTypeHeaderDelegate.toString(mediaType));

    mediaType = new MediaType("text", "beer+json", Collections.singletonMap("charset", "utf-8"));
    assertEquals("text/beer+json;charset=utf-8", mediaTypeHeaderDelegate.toString(mediaType));
  }
}
