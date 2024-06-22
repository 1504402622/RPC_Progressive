package cn.glfs.common;

import java.util.Objects;

public class URL {
    private String ip;
    private Integer port;
    public URL(String host, Integer port) {
        this.ip = host;
        this.port = port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip,port);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        URL url = (URL) o;
        return Objects.equals(ip, url.ip) && Objects.equals(port, url.port);
    }
}
