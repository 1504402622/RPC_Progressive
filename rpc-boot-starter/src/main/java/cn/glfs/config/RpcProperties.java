package cn.glfs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "rpc")
public class RpcProperties {


    private Integer port;

    private String serialization;

    private String proxy;

    private String invoke;

    private RegistryProperties registry;

    private int corePollSize = 5;

    private int maximumPoolSize = 10;


    public int getCorePollSize() {
        return corePollSize;
    }

    public void setCorePollSize(int corePollSize) {
        this.corePollSize = corePollSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public RegistryProperties getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryProperties registry) {
        this.registry = registry;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getInvoke() {
        return invoke;
    }

    public void setInvoke(String invoke) {
        this.invoke = invoke;
    }
}
