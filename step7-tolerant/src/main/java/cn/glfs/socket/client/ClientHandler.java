package cn.glfs.socket.client;

import cn.glfs.common.RpcFuture;
import cn.glfs.common.RpcRequestHolder;
import cn.glfs.socket.codec.RpcProtocol;
import cn.glfs.socket.codec.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> rpcResponseRpcProtocol) throws Exception {
        long requestId = rpcResponseRpcProtocol.getHeader().getRequestId();
        // 因任务完成,将该id对应的异步任务删除
        RpcFuture<RpcResponse> future = RpcRequestHolder.REQUEST_MAP.remove(requestId);
        // 将promise任务设置为成功
        future.getPromise().setSuccess(rpcResponseRpcProtocol.getBody());
    }
}
