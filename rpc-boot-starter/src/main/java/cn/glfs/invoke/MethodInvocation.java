package cn.glfs.invoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MethodInvocation {

    private Object o;

    private Method method;
    public MethodInvocation(Object o, Method method){
        this.o = o;
        this.method = method;
    }
    public Object invoke(Object parameter) throws InvocationTargetException, IllegalAccessException {

        return method.invoke(o,parameter);
    }
}
