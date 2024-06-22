package cn.glfs.tolerant;

import cn.glfs.common.constants.FaultTolerant;

import java.util.HashMap;
import java.util.Map;

public class FaultTolerantFactory {
    private static Map<FaultTolerant,FaultTolerantStrategy> faultTolerantStrategyMap = new HashMap<>();
    static {
        faultTolerantStrategyMap.put(FaultTolerant.Failover,new FailoverFaultTolerantStrategy());
        faultTolerantStrategyMap.put(FaultTolerant.FailFast,new FailFastFaultTolerantStrategy());
    }
    public static FaultTolerantStrategy get(FaultTolerant faultTolerant){
        return faultTolerantStrategyMap.get(faultTolerant);
    }
}
