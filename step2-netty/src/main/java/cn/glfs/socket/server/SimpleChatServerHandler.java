package cn.glfs.socket.server;

import cn.glfs.socket.codec.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SimpleChatServerHandler extends SimpleChannelInboundHandler<RpcProtocol> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol rpcRequestRpcProtocol) throws Exception {
        System.out.println(rpcRequestRpcProtocol.getBody());
    }
}