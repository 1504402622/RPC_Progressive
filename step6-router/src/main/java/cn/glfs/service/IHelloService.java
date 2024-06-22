package cn.glfs.service;

import cn.glfs.annotation.RpcReference;

@RpcReference
public interface IHelloService {
    Object hello(String text);
}
