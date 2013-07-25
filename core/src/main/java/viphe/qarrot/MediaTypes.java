package viphe.qarrot;

import com.rabbitmq.client.AMQP;

import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;
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

    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_ENCODING);


    public static MediaType valueOf(AMQP.BasicProperties properties) {
        return properties == null ? null : withCharset(properties.getContentType(), properties.getContentEncoding());
    }

    public static String getCharset(MediaType mediaType) {
        return mediaType == null ? null : mediaType.getParameters().get("charset");
    }

    public static String getDefaultedCharset(MediaType mediaType) {
        String charset = getCharset(mediaType);
        return charset == null ? DEFAULT_ENCODING : charset;
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
