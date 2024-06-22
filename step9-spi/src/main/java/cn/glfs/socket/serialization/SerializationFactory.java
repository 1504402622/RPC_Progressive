package cn.glfs.socket.serialization;

import cn.glfs.common.constants.RpcSerialization;
import cn.glfs.spi.ExtensionLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 序列化工厂,用于获取不同序列化器
 */
public class SerializationFactory {
    public static cn.glfs.socket.serialization.RpcSerialization get(RpcSerialization serialization){
        return ExtensionLoader.getInstance().get(serialization.name);
    }

    public static cn.glfs.socket.serialization.RpcSerialization get(String name){
        return ExtensionLoader.getInstance().get(name);
    }

    public static void init() throws IOException, ClassNotFoundException {
        ExtensionLoader.getInstance().loadExtension(cn.glfs.socket.serialization.RpcSerialization.class);
    }

//    private static Map<RpcSerialization, cn.glfs.socket.serialization.RpcSerialization>serializationMap
//            = new HashMap<>();
//    static {
//        serializationMap.put(RpcSerialization.JSON,new JsonSerialization());
//    }
//    public static cn.glfs.socket.serialization.RpcSerialization get(RpcSerialization serialization){
//        return serializationMap.get(serialization);
//    }
}
