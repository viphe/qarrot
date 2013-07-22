package viphe.qarrot;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/21/13
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
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
