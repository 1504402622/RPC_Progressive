package cn.glfs.socket.serialization;

import java.io.IOException;

/**
 * 序列接口
 */
public interface RpcSerialization {
    // 序列化
    <T> byte[] serialize(T obj) throws IOException;
    // 反序列化
    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}
