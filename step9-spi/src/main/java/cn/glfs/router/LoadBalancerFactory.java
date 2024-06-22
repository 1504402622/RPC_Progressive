package cn.glfs.router;


import cn.glfs.common.constants.LoadBalance;
import cn.glfs.spi.ExtensionLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoadBalancerFactory {

    public static LoadBalancer get(LoadBalance loadBalance){
        return ExtensionLoader.getInstance().get(loadBalance.name);
    }

    public static LoadBalancer get(String name){
        return ExtensionLoader.getInstance().get(name);
    }

    public static void init() throws IOException, ClassNotFoundException {
        ExtensionLoader.getInstance().loadExtension(LoadBalancer.class);
    }
//    private static Map<LoadBalance, LoadBalancer> loadBalancerMap = new HashMap();
//    static {
//        loadBalancerMap.put(LoadBalance.Round,new RoundRobinLoadBalancer());
//
//    }
//    public static LoadBalancer get(LoadBalance loadBalance){
//        return loadBalancerMap.get(loadBalance);
//    }
}

