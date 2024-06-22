package cn.glfs.common.constants;

import java.util.Objects;

public class Host {

    private final String ip;

    private final Integer port;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Host ip = (Host) object;
        return Objects.equals(this.ip, ip.ip) && Objects.equals(port, ip.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    public Host(String host, Integer port) {
        this.ip = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return "Host{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}