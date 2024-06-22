package cn.glfs.common;

import java.util.Objects;

public class ServiceName {
    private final String name;

    public ServiceName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ServiceName{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceName that = (ServiceName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
