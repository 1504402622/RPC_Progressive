package cn.glfs.service;

import cn.glfs.annotation.RpcService;

@RpcService
public class HelloService implements IHelloService{

    @Override
    public Object hello(String text) {
        return "result:"+text;
    }
}
