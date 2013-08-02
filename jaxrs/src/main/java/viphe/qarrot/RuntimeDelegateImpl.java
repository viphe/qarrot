package viphe.qarrot;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * A limited implementation of RuntimeDelegate.
 */
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
        if (MediaType.class.isAssignableFrom(type)) {
            return (HeaderDelegate<T>) MediaTypeHeaderDelegate.INSTANCE;

        } else {
            return null;
        }
    }
}
