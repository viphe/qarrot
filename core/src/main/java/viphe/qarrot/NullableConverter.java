package viphe.qarrot;

import com.google.common.io.CountingInputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 *
 */
public class NullableConverter<T> implements MessageBodyWriter<T>, MessageBodyReader<T> {

    private final MessageBodyReader<T> reader;
    private final MessageBodyWriter<T> writer;


    public NullableConverter(MessageBodyReader<T> reader, MessageBodyWriter<T> writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return reader.isReadable(type, genericType, annotations, mediaType);
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        CountingInputStream countingInputStream = new CountingInputStream(entityStream);
        try {
            return reader.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
        } catch (EOFException e) {
            if (countingInputStream.getCount() == 0) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return writer.isWriteable(type, genericType, annotations, mediaType);
    }

    @Override
    public long getSize(T o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return writer.getSize(o, type, genericType, annotations, mediaType);
    }

    @Override
    public void writeTo(T o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        if (o == null) return;
        writer.writeTo(o, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }
}
