package viphe.qarrot;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/20/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MediaTypes {

    public static String getCharset(MediaType mediaType) {
        return mediaType.getParameters().get("charset");
    }

    public static boolean isError(MediaType mediaType) {
        return Boolean.valueOf(mediaType.getParameters().get("error"));
    }

    public static MediaType asError(MediaType mediaType) {
        Map<String, String> parameters = new LinkedHashMap<String, String>(mediaType.getParameters());
        parameters.put("charset", "true");
        return new MediaType(mediaType.getType(), mediaType.getSubtype(), parameters);
    }

    public static MediaType withCharset(MediaType mediaType, String encoding) {
        Map<String, String> parameters = new LinkedHashMap<String, String>(mediaType.getParameters());
        if (encoding != null) {
            parameters.put("charset", encoding);
        }
        return new MediaType(mediaType.getType(), mediaType.getSubtype(), parameters);
    }

    public static MediaType withCharset(String contentType, String encoding) {
        MediaType mediaType = MediaType.valueOf(contentType);
        return withCharset(mediaType, encoding);
    }
}
