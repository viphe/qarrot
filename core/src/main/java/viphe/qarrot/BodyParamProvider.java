package viphe.qarrot;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class BodyParamProvider<T> extends ParamProvider<T> {

    private final MediaType mediaType;


    public BodyParamProvider(Class<T> resultType, MediaType mediaType) {
        super(resultType);
        if (mediaType == null) throw new NullPointerException("'mediaType' cannot be null");
        this.mediaType = mediaType;
    }

    @Override
    public T get(Event event) throws IOException {
        MediaType consumedMediaType =
            this.mediaType == null ? MediaType.valueOf(event.getProperties().getContentType()) : mediaType;
        return event.getQarrot().fromBytes(consumedMediaType, event.getBody(), resultType);
    }
}
