package cn.glfs.proxy;

import cn.glfs.annotation.RpcReference;


public interface IProxy {

    <T> T getProxy(Class claz, RpcReference rpcReference);
}
