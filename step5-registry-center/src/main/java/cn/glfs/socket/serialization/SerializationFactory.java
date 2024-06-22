package cn.glfs.socket.serialization;

import cn.glfs.common.constants.RpcSerialization;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化工厂,用于获取不同序列化器
 */
public class SerializationFactory {
    private static Map<RpcSerialization, cn.glfs.socket.serialization.RpcSerialization>serializationMap
            = new HashMap<>();
    static {
        serializationMap.put(RpcSerialization.JSON,new JsonSerialization());
    }
    public static cn.glfs.socket.serialization.RpcSerialization get(RpcSerialization serialization){
        return serializationMap.get(serialization);
    }
}
