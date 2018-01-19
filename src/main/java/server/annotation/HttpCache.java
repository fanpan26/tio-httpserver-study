package server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author fyp
 * @Description
 * @Date Created at 2018/1/18 16:22
 * @Project tio-http-server
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpCache {
    int timeToIdleSeconds() default 10;
    int timeToLiveSeconds() default 0;
    String[] params();
}
