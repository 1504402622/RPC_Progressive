package cn.glfs.common;

import java.util.Objects;

public class URL {

    private String ip;

    private Integer port;

    private String serviceName;

    private String version;

    public URL() {
    }

    public URL(String host, Integer port) {
        this.ip = host;
        this.port = port;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getVersion() {
        return version;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        URL url = (URL) object;
        return Objects.equals(ip, url.ip) && Objects.equals(port, url.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }


    @Override
    public String toString() {
        return "URL{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", serviceName='" + serviceName + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
