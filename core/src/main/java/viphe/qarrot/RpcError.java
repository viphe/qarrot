package viphe.qarrot;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class RpcError {

    public String error;
    public String trace;


    public RpcError() {
    }

    public RpcError(String error, String trace) {
        this.error = error;
        this.trace = trace;
    }

    public RpcError(String error, Throwable t) {
        this.error = error;
        this.trace = ExceptionUtils.getStackTrace(t);
    }
}
