package cn.glfs.common;

import cn.glfs.common.constants.Host;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    // 相同方法名对应不同的机器地址,注册中心管理方法信息
    public static ConcurrentHashMap<ServiceName, List<URL>> SERVICE_URLS = new ConcurrentHashMap<>();

    // 主机对应Future避免重复连接
    public static Map<Host, ChannelFuture> CHANNEL_FUTURE_MAP = new HashMap<Host, ChannelFuture>();

    // 已订阅的节点地址
    public static List<URL> SUBSCRIBE_SERVICE_LIST = new ArrayList<>();

    // 设置静态bootstrap,避免重复构建bootstrap
    public static Bootstrap BOOT_STRAP;

    // service$version
    public static Map<String,Object> SERVICE_MAP = new HashMap<>();
}
