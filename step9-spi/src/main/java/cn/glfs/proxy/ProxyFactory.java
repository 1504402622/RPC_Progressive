package cn.glfs.proxy;

import cn.glfs.common.constants.RpcProxy;
import cn.glfs.proxy.cglib.CgLibProxyFactory;
import cn.glfs.spi.ExtensionLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProxyFactory {
    public static IProxy get(RpcProxy rpcProxy){
        return ExtensionLoader.getInstance().get(rpcProxy.name);
    }
    public static IProxy get(String name){
        return ExtensionLoader.getInstance().get(name);
    }
    public static void init() throws IOException, ClassNotFoundException {
        ExtensionLoader.getInstance().loadExtension(IProxy.class);
    }

//    private static Map<RpcProxy,IProxy> proxyIProxyMap = new HashMap<RpcProxy, IProxy>();
//    static {
//        proxyIProxyMap.put(RpcProxy.CG_LIB,new CgLibProxyFactory());
//    }
//    public static IProxy get(RpcProxy rpcProxy){
//        return proxyIProxyMap.get(rpcProxy);
//    }
}
