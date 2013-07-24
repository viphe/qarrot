package viphe.qarrot;

import java.io.IOException;

public abstract class ParamProvider<T> {

    protected Class<T> resultType;

    protected ParamProvider(Class<T> resultType) {
        this.resultType = resultType;
    }

    public abstract T get(Event event) throws IOException;
}
