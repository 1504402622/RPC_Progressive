package cn.glfs.tolerant;


public interface FaultTolerantStrategy {

    Object handler(FaultContext faultContext) throws Exception;
}
