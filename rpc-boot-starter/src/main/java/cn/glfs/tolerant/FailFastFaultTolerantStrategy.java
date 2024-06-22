package cn.glfs.tolerant;


public class FailFastFaultTolerantStrategy implements FaultTolerantStrategy {

    @Override
    public Object handler(FaultContext faultContext) throws Exception {
        throw faultContext.getException();
    }
}
