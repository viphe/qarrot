package viphe.qarrot;

import org.apache.commons.io.IOUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 *
 */
public class ByteArrayConverter implements MessageBodyWriter<byte[]>, MessageBodyReader<byte[]> {

    public static final ByteArrayConverter INSTANCE = new ByteArrayConverter();

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return byte[].class.equals(type);
    }

    @Override
    public byte[] readFrom(Class<byte[]> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(entityStream.available());
        IOUtils.copy(entityStream, baos);
        return baos.toByteArray();
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return byte[].class.equals(type);
    }

    @Override
    public long getSize(byte[] bytes, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return bytes.length;
    }

    @Override
    public void writeTo(byte[] bytes, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        IOUtils.copy(new ByteArrayInputStream(bytes), entityStream);
    }
}
