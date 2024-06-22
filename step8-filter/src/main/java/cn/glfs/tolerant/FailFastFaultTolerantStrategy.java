package cn.glfs.tolerant;

/**
 * 快速失败策略
 */
public class FailFastFaultTolerantStrategy implements FaultTolerantStrategy{
    @Override
    public Object handler(FaultContext faultContext) throws Exception {
        return faultContext.getException();
    }

}
