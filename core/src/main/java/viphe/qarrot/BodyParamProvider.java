package viphe.qarrot;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/21/13
 * Time: 12:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class BodyParamProvider implements ParamProvider {

    private final MediaType mediaType;


    public BodyParamProvider(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public Object get(Event event) throws IOException {
        MediaType consumedMediaType =
            this.mediaType == null ? MediaType.valueOf(event.getProperties().getContentType()) : mediaType;

        if (MediaType.TEXT_PLAIN_TYPE.isCompatible(consumedMediaType)) {
            String encoding = MediaTypes.getCharset(consumedMediaType);
            return encoding == null ? new String(event.getBody()) : new String(event.getBody(), encoding);
        } else {
            throw new RuntimeException("unsupported " + consumedMediaType + " ==> String");
        }
    }
}
