package cn.glfs.common;

import io.netty.channel.ChannelFuture;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    // sevice -> url
    public static Map<ServiceName,URL> services = new HashMap<ServiceName, URL>();
    // url-> channelFuture
    public static Map<URL, ChannelFuture> channelFutureMap = new HashMap<URL, ChannelFuture>();
}
