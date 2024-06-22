package cn.glfs.socket.server;

import cn.glfs.common.constants.MsgType;
import cn.glfs.common.constants.RpcInvoker;
import cn.glfs.invoke.Invocation;
import cn.glfs.invoke.InvokerFactory;
import cn.glfs.invoke.Invoker;
import cn.glfs.socket.codec.MsgHeader;
import cn.glfs.socket.codec.RpcProtocol;
import cn.glfs.socket.codec.RpcRequest;
import cn.glfs.socket.codec.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> rpcProtocol) throws Exception {
        final RpcRequest rpcRequest = rpcProtocol.getBody();
        final RpcResponse response = new RpcResponse();
        final RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
        final MsgHeader header = rpcProtocol.getHeader();
        header.setMsgType((byte) MsgType.RESPONSE.ordinal());// 枚举常量在枚举声明中的位置索引，从0开始计数。0:请求,1:响应,2:心跳
        responseRpcProtocol.setHeader(header);
        final Invoker invoker = InvokerFactory.get(RpcInvoker.JDK);
        try {
            final Object data = invoker.invoke(new Invocation(rpcRequest));
            response.setData(data);
        }catch (Exception e){
            response.setException(e);
        }
        responseRpcProtocol.setBody(response);
        channelHandlerContext.writeAndFlush(responseRpcProtocol);
    }
}
