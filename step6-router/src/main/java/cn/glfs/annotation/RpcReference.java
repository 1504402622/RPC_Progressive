package cn.glfs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 服务发现
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
public @interface RpcReference {

    String version() default "1.0";
    //
    long time() default 3000;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
