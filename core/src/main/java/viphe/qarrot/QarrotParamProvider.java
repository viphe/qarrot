package viphe.qarrot;

public class QarrotParamProvider extends ParamProvider<Qarrot> {

    public QarrotParamProvider() {
        super(Qarrot.class);
    }

    @Override
    public Qarrot get(Event event) {
        return resultType.cast(event.getQarrot());
    }
}
