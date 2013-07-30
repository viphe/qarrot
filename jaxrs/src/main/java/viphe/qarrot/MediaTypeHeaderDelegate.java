package viphe.qarrot;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class MediaTypeHeaderDelegate implements RuntimeDelegate.HeaderDelegate<MediaType> {

    private static final Pattern PATTERN = Pattern.compile("^(.*?)/(.*?)(;(.*))?$");
    private static final Map<String, String> EMPTY_PARAMETERS = Collections.emptyMap();

    public static final MediaTypeHeaderDelegate INSTANCE = new MediaTypeHeaderDelegate();


    @Override
    public MediaType fromString(String value) throws IllegalArgumentException {
        Matcher matcher = PATTERN.matcher(value);
        if (matcher.matches()) {
            String type = decode(matcher.group(1));
            String subtype = decode(matcher.group(2));

            Map<String, String> parameters = null;
            if (matcher.group(4) != null) {
                String[] paramTokens = matcher.group(4).split(";", -1);
                for (String paramToken : paramTokens) {
                    String[] keyValue = paramToken.split("=");
                    String paramKey, paramValue;
                    paramKey = decode(keyValue[0]);
                    paramValue = keyValue.length <= 1 ? null : decode(keyValue[1]);
                    if (parameters == null) parameters = new LinkedHashMap<String, String>();
                    parameters.put(paramKey, paramValue);
                }
            }
            if (parameters == null) parameters = EMPTY_PARAMETERS;

            return new MediaType(type, subtype, parameters);

        } else {
            throw new IllegalArgumentException("bad media type: " + value);
        }
    }

    @Override
    public String toString(MediaType value) {
        StringBuilder sb = new StringBuilder(encode(value.getType())).append('/').append(encode(value.getSubtype()));

        for (Map.Entry<String, String> e : value.getParameters().entrySet()) {
            sb.append(';');
            sb.append(encode(e.getKey())).append('=').append(encode(e.getValue()));
        }

        return sb.toString();
    }

    private String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("unexpected error", e);
        }
    }

    private String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("unexpected error", uee);
        }
    }
}
