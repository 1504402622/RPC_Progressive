package cn.glfs.proxy.cglib;

import cn.glfs.proxy.IProxy;
import net.sf.cglib.proxy.Enhancer;

public class CgLibProxyFactory implements IProxy {
    @Override
    public <T> T getProxy(Class<T> claz) throws InstantiationException, IllegalAccessException {
        Enhancer enhancer = new Enhancer();
        // 设置目标类
        enhancer.setSuperclass(claz);
        // 设置回调对象
        enhancer.setCallback(new CgLibProxy(claz.newInstance()));
        // 返回代理对象
        return (T) enhancer.create();
    }
}
