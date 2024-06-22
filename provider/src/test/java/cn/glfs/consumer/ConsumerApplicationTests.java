package cn.glfs.consumer;

import cn.glfs.common.RpcRequestHolder;
import cn.glfs.common.constants.MsgType;
import cn.glfs.common.constants.ProtocolConstants;
import cn.glfs.socket.codec.MsgHeader;
import cn.glfs.socket.codec.RpcProtocol;
import cn.glfs.socket.codec.RpcRequest;
import cn.glfs.socket.serialization.hessian.HessianSerialization;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ConsumerApplicationTests {

    @Test
    void contextLoads() {


    }

    public static void main(String[] args) throws IOException {
        final HessianSerialization serialization = new HessianSerialization();

        final MyObject myObject = new MyObject();

        int count = 100000;

        final RpcProtocol rpcProtocol = new RpcProtocol();
        // 构建消息头
        MsgHeader header = new MsgHeader();
        long requestId = RpcRequestHolder.getRequestId();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        header.setMsgType((byte) MsgType.REQUEST.ordinal());
        header.setStatus((byte) 0x1);
        rpcProtocol.setHeader(header);
        final RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName("org.xhy.HelloServer");
        rpcRequest.setMethodCode(1230);
        rpcRequest.setMethodName("method");
        rpcRequest.setServiceVersion("1.0");
        rpcRequest.setParameterTypes(MyObject.class);
        rpcRequest.setParameter(myObject);

        rpcProtocol.setBody(rpcRequest);

        // jdk序列化 序列化 10w 次 3173
        // fastjson 序列化 10w 次 968
        // hessian 序列化 10w 次 1500
        // kryo 序列化 10w 次 832
//        Long start = System.currentTimeMillis();
//        for (int i = 0; i < count; i++) {
//            serialization.serialize(rpcProtocol);
//        }
//        Long end = System.currentTimeMillis();
//        System.out.println(end-start);

        // jdk 反序列化 10w 次 10640
        // fastjson 反序列化 10w 次 2140
        // kyro 反序列化 10w 次 1262
        // hessian 反序列化 10w次 1582
        final byte[] data = serialization.serialize(rpcProtocol);
        Long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            serialization.deserialize(data,RpcRequest.class);
        }
        Long end = System.currentTimeMillis();
        System.out.println(end-start);
    }
}

class MyObject implements Serializable {
    String name = "xhy";

    Integer age = 18;

    List<Integer> ids = new ArrayList<>();

    public MyObject(){
        for (int i = 0; i < 100; i++) {
            ids.add(i);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}