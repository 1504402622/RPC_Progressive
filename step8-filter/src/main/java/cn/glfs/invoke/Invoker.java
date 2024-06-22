package cn.glfs.invoke;

import java.lang.reflect.InvocationTargetException;

public interface Invoker {
    Object invoke(Invocation invocation) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException;
}
