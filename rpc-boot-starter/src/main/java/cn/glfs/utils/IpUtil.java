package cn.glfs.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class IpUtil {

    public static String getIP(){
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return inetAddress.getHostAddress();
    }
}
