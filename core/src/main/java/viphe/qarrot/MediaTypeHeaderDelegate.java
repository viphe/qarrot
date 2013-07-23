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
            String type = matcher.group(1);
            String subtype = matcher.group(2);

            Map<String, String> parameters = null;
            if (matcher.group(4) != null) {
                String[] paramTokens = matcher.group(4).split("&", -1);
                for (String paramToken : paramTokens) {
                    String[] keyValue = paramToken.split("=");
                    String paramKey, paramValue;
                    try {
                        paramKey = URLDecoder.decode(keyValue[0], "UTF-8");
                        paramValue = keyValue.length == 0 ? null : URLDecoder.decode(keyValue[1], "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("unexpected error", e);
                    }
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
        StringBuilder sb = new StringBuilder(value.getType()).append('/').append(value.getSubtype());

        boolean first = true;
        for (Map.Entry<String, String> e : value.getParameters().entrySet()) {
            if (first) {
                first = false;
                sb.append(';');
            } else {
                sb.append('&');
            }

            try {
                sb.append(URLEncoder.encode(e.getKey(), "UTF-8"))
                    .append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("unexpected error", uee);
            }
        }

        return sb.toString();
    }
}
