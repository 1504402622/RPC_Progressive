package cn.glfs.proxy.cglib;

import cn.glfs.annotation.RpcReference;
import cn.glfs.proxy.IProxy;
import net.sf.cglib.proxy.Enhancer;

public class CgLibProxyFactory<T> implements IProxy {


    public <T> T getProxy(Class claz, RpcReference rpcReference)  {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(claz);
        enhancer.setCallback(new CgLibProxy(claz,rpcReference));
        return (T) enhancer.create();
    }
}
