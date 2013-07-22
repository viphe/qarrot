package viphe.qarrot;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target(ElementType.METHOD)
public @interface RouteOut {

    String value();
}
