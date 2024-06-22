package cn.glfs.invoke;

import cn.glfs.common.Cache;
import cn.glfs.utils.ServiceNameBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JdkReflectionInvoker implements Invoker {
    private Map<Integer,MethodInvocation> methodCache = new HashMap();

    @Override
    public Object invoke(Invocation invocation) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        final Integer methodCode = invocation.getMethodCode();
        if (!methodCache.containsKey(methodCode)) {
            // 服务名$版本号，从
            final String key = ServiceNameBuilder.buildServiceKey(invocation.getClassName(), invocation.getServiceVersion());
            // cn.glfs.service.IHelloService$1.0
            Object bean = Cache.SERVICE_MAP.get(key);

            final Class<?> aClass = bean.getClass();
            final Method method = aClass.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            methodCache.put(methodCode,new MethodInvocation(aClass.newInstance(),method));
        }
        final MethodInvocation methodInvocation = methodCache.get(methodCode);
        return methodInvocation.invoke(invocation.getParameter());
    }
}
