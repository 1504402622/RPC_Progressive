package cn.glfs.provider.service;

import cn.glfs.annotation.RpcService;
import org.springframework.stereotype.Component;

import cn.glfs.service.HelloService;


@Component
@RpcService
public class TestService implements HelloService{
    @Override
    public Object hello(String arg) {
        return arg + "provider2";
    }
}
