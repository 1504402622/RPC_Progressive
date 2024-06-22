package cn.glfs.register;


import cn.glfs.common.URL;

import java.util.List;

public interface RegistryService {
    void register(URL url) throws Exception;
    void unRegister(URL url) throws Exception;
    List<URL> discoveries(String serviceName,String version) throws Exception;
    void subscribe(URL url) throws Exception;
    void unSubscribe(URL url);

}
