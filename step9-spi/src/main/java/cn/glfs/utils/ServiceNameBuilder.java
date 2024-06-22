package cn.glfs.utils;


public class ServiceNameBuilder {
    // serviceName$serviceVersion
    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("$", serviceName, serviceVersion);
    }
}
