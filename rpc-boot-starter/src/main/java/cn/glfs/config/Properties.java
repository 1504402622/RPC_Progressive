package cn.glfs.config;


public class  Properties {

    private static Integer port;

    private static String serialization;

    private static String proxy;

    private static String invoke;

    private static RegistryProperties register;

    private static int corePollSize;

    private static int maximumPoolSize;

    public static int getCorePollSize() {
        return corePollSize;
    }

    public static void setCorePollSize(int corePollSize) {
        Properties.corePollSize = corePollSize;
    }

    public static int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public static void setMaximumPoolSize(int maximumPoolSize) {
        Properties.maximumPoolSize = maximumPoolSize;
    }

    public static String getSerialization() {
        return serialization;
    }

    public static void setSerialization(String serialization) {
        Properties.serialization = serialization;
    }

    public static String getProxy() {
        return proxy;
    }

    public static void setProxy(String proxy) {
        Properties.proxy = proxy;
    }

    public static String getInvoke() {
        return invoke;
    }

    public static void setInvoke(String invoke) {
        Properties.invoke = invoke;
    }




    public static Integer getPort() {
        return port;
    }

    public static void setPort(Integer port) {
        Properties.port = port;
    }

    public static RegistryProperties getRegister() {
        return register;
    }

    public static void setRegister(RegistryProperties register) {
        Properties.register = register;
    }
}
