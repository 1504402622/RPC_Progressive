package cn.glfs.proxy;

public interface IProxy {
    <T> T getProxy(Class<T> claz) throws InstantiationException, IllegalAccessException;
}
