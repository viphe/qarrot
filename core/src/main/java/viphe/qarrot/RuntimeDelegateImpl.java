package viphe.qarrot;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

public class RuntimeDelegateImpl extends RuntimeDelegate {

    @Override
    public UriBuilder createUriBuilder() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Response.ResponseBuilder createResponseBuilder() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Variant.VariantListBuilder createVariantListBuilder() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public <T> T createEndpoint(Application application, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) {
        return new HeaderDelegate<T>() {
            @Override
            public T fromString(String value) throws IllegalArgumentException {
                throw new RuntimeException("not implemented");
            }

            @Override
            public String toString(T value) {
                throw new RuntimeException("not implemented");
            }
        };
    }
}
