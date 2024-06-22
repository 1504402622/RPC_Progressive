package cn.glfs.invoke;

import cn.glfs.common.constants.RpcInvoker;


import java.util.HashMap;
import java.util.Map;

public class InvokerFactory {
    public static Map<RpcInvoker, Invoker> invokerInvokerMap = new HashMap();
    static {
        invokerInvokerMap.put(RpcInvoker.JDK,new JdkReflectionInvoker());
    }
    public static Invoker get(RpcInvoker rpcInvoker){
        return invokerInvokerMap.get(rpcInvoker);
    }
}
