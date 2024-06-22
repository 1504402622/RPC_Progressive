package cn.glfs.socket.serialization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import java.io.IOException;

/**
 * json方式序列化与反序列化
 */
public class JsonSerialization implements RpcSerialization{
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        try {
            // 使用Fastjson将对象序列化为Json格式的字符串
            String jsonString = JSON.toJSONString(obj);
            // 将Json字符串转为byte数组
            return jsonString.getBytes("UTF-8");
        }catch (JSONException e) {
            // 捕获序列化过程中可能发生的异常
            throw new IOException("Error serializing object to JSON", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        try {
            // 将byte数组转为JSON字符串
            String jsonString = new String(data,"UTF-8");
            // 使用Fastjson将Json字符串反序列化为对象
            return JSON.parseObject(jsonString,clz);
        }catch (JSONException e){
            // 捕获反序列化过程中可能发生的异常
            throw new IOException("Error deserializing JSON to object", e);
        }
    }
}
