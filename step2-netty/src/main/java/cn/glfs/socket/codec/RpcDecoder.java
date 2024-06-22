package cn.glfs.socket.codec;

import cn.glfs.common.constants.MsgType;
import cn.glfs.common.constants.ProtocolConstants;
import cn.glfs.common.constants.RpcSerialization;
import cn.glfs.socket.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码器
 */
public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        // 如果可读字节数少于协议头长度，说明还没有接收完整个协议头，直接返回
        // 在这种情况下，不会将任何对象添加到输出列表（out），也不会将消息传递给下一个入站处理器
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        // 标记当前读取位置，便于后面回退
        in.markReaderIndex();

        // 读取魔数字段
        short magic = in.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }
        // 读取版本字段
        byte version = in.readByte();
        // 读取消息类型
        byte msgType = in.readByte();
        // 读取响应状态
        byte status = in.readByte();
        // 读取请求 ID
        long requestId = in.readLong();
        // 获取序列化算法长度
        final int len = in.readInt();
        if (in.readableBytes() < len){
            in.resetReaderIndex();
            return;
        }
        final byte[] bytes = new byte[len];
        in.readBytes(bytes);
        final String serialization = new String(bytes);
        // 读取消息体长度
        int dataLength = in.readInt();
        // 如果可读字节数小于消息体长度，说明还没有接收完整个消息体，回退并返回
        if (in.readableBytes() < dataLength) {
            // 回退标记位置
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        // 读取数据
        in.readBytes(data);

        // 处理消息的类型
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }

        // 构建消息头
        MsgHeader header = new MsgHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setSerialization(bytes);
        header.setSerializationLen(len);
        header.setMsgLen(dataLength);
        // 获取对内容进行反序列化器，用指定序列化方式
        cn.glfs.socket.serialization.RpcSerialization rpcSerialization = SerializationFactory.get(RpcSerialization.get(serialization));
        final String body = rpcSerialization.deserialize(data, String.class);
        RpcProtocol protocol = new RpcProtocol<>();
        protocol.setHeader(header);
        protocol.setBody(body);
        out.add(protocol);
    }
}
