package cn.glfs.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={TYPE})
public @interface RpcService {
    /**
     * 指定实现方,默认为实现接口中第一个
     * 默认值为void.class，表示没有指定具体的接口类型
     * 可以通过rpcService.getServiceInterface()方法获取到HelloService类实现的服务接口类型IHelloService
     * @return
     */
    Class<?> serviceInterface() default void.class;

    String version() default "1.0";
}
