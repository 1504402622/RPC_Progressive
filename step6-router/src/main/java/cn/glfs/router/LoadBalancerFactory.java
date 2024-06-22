package cn.glfs.router;


import cn.glfs.common.constants.LoadBalance;

import java.util.HashMap;
import java.util.Map;

public class LoadBalancerFactory {
    private static Map<LoadBalance, LoadBalancer> loadBalancerMap = new HashMap();
    static {
        loadBalancerMap.put(LoadBalance.Round,new RoundRobinLoadBalancer());

    }
    public static LoadBalancer get(LoadBalance loadBalance){
        return loadBalancerMap.get(loadBalance);
    }
}

