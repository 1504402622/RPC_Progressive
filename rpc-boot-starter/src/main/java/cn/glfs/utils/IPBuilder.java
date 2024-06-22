package cn.glfs.utils;


public class IPBuilder {

    private static final String symbol = ":";
    public static String buildIp(String host,Integer port){
        return host + symbol + port;
    }
}
