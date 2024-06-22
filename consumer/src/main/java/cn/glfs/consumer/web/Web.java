package cn.glfs.consumer.web;

import cn.glfs.annotation.RpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.glfs.service.HelloService;


@RestController
@RequestMapping("/test")
public class Web {

    @RpcReference
    HelloService helloService;

    @GetMapping("/set")
    public Object hello(String arg){
        return helloService.hello(" hello~");
    }
}
