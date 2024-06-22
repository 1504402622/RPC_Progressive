package cn.glfs.service;

import cn.glfs.annotation.RpcService;

@RpcService
public class HelloService2 implements IHelloService{

    @Override
    public Object hello(String text) {
        return "service2 result:"+text;
    }
}
