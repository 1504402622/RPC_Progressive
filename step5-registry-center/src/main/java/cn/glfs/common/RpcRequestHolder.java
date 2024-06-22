package cn.glfs.common;

import cn.glfs.socket.codec.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求头辅助类，为Reqeust提供id
 */
public class RpcRequestHolder{
    // 请求id,定义一个静态的原子长整型变量 REQUEST_ID_GEN，初始值为0。这样的定义通常用于生成具有唯一标识符的请求ID
    public final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);
    // 绑定请求
    public static final Map<Long,RpcFuture <RpcResponse>> REQUEST_MAP = new ConcurrentHashMap();

    public static Long getRequestId(){
        if(REQUEST_ID_GEN.longValue() == Long.MAX_VALUE){
            REQUEST_ID_GEN.set(0);
        }
        // 递增++
        return REQUEST_ID_GEN.incrementAndGet();
    }
}
