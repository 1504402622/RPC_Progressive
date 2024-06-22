package cn.glfs.register;


import cn.glfs.common.Cache;
import cn.glfs.common.ServiceName;
import cn.glfs.common.URL;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractZookeeperRegistry implements RegistryService{

    /**
     * 将URL注册到Zookeeper注册表中,(服务名,服务器列表)
     */
    @Override
    public void register(URL url) throws Exception{
        final ServiceName serviceName = new ServiceName(url.getServiceName(),url.getVersion());
        if(!Cache.SERVICE_URLS.containsKey(serviceName)){
            Cache.SERVICE_URLS.put(serviceName,new ArrayList<>());
        }
        Cache.SERVICE_URLS.get(serviceName).add(url);
    }

    /**
     * 从Zookeeper注册表中取消URL
     */
    @Override
    public void unRegister(URL url) throws Exception{
        final ServiceName serviceName = new ServiceName(url.getServiceName(), url.getVersion());
        if (Cache.SERVICE_URLS.containsKey(serviceName)) {
            Cache.SERVICE_URLS.get(serviceName).remove(url);
        }
    }

    /**
     * 从Zookeeper注册表中发现给定服务名称进而版本的URL
     */
    @Override
    public List<URL> discoveries(String serviceName, String version) throws Exception {
        final ServiceName key = new ServiceName(serviceName, version);
        List<URL> urls = Cache.SERVICE_URLS.get(key);
        return urls;
    }
}
