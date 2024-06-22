package cn.glfs.service;

public class HelloService implements IHelloService{

    @Override
    public Object hello(String text) {
        return "result:"+text;
    }
}
