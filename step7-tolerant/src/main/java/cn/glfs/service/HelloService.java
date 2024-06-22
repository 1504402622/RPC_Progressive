package cn.glfs.service;

import cn.glfs.annotation.RpcService;

@RpcService
public class HelloService implements IHelloService{

    @Override
    public Object hello(String text) {
        int i=0;
        // 故障
        System.out.println(1/i);
        return "result:"+text;
    }
}
