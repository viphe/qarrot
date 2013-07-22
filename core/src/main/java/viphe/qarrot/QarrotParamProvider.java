package viphe.qarrot;

/**
 * Created with IntelliJ IDEA.
 * User: viphe
 * Date: 7/21/13
 * Time: 9:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class QarrotParamProvider implements ParamProvider {
    @Override
    public Object get(Event event) {
        return event.getQarrot();
    }
}
