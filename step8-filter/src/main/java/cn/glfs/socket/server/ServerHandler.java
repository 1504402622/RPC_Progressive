package cn.glfs.socket.server;

import cn.glfs.common.constants.MsgType;
import cn.glfs.common.constants.RpcInvoker;
import cn.glfs.filter.*;
import cn.glfs.invoke.Invocation;
import cn.glfs.invoke.Invoker;
import cn.glfs.invoke.InvokerFactory;
import cn.glfs.socket.codec.MsgHeader;
import cn.glfs.socket.codec.RpcProtocol;
import cn.glfs.socket.codec.RpcRequest;
import cn.glfs.socket.codec.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

public class ServerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> rpcProtocol) throws Exception {
        final MsgHeader header = rpcProtocol.getHeader();
        final RpcRequest rpcRequest = rpcProtocol.getBody();

        final RpcResponse response = new RpcResponse();
        final RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();

        header.setMsgType((byte) MsgType.RESPONSE.ordinal());// 枚举常量在枚举声明中的位置索引，从0开始计数。0:请求,1:响应,2:心跳
        responseRpcProtocol.setHeader(header);

        final Invoker invoker = InvokerFactory.get(RpcInvoker.JDK);
        // 拦截器（返回响应前）
        final List<Filter> serverBeforeFilters = FilterFactory.getServerBeforeFilters();
        if (!serverBeforeFilters.isEmpty()){
            final FilterData<RpcRequest> rpcRequestFilterData = new FilterData<>(rpcRequest);
            final FilterLoader filterLoader = new FilterLoader();
            filterLoader.addFilter(serverBeforeFilters);
            final FilterResponse filterResponse = filterLoader.doFilter(rpcRequestFilterData);
            if (!filterResponse.getResult()) {
                throw filterResponse.getException();
            }
        }

        try {
            // 这里传过来 className = IHelloService
            final Object data = invoker.invoke(new Invocation(rpcRequest));
            response.setData(data);
        }catch (Exception e){
            response.setException(e);
        }

        // 拦截器(返回响应后)
        final List<Filter> serverAfterFilters = FilterFactory.getServerAfterFilters();
        if (!serverAfterFilters.isEmpty()){
            final FilterData<RpcResponse> rpcResponseFilterData = new FilterData<>(response);
            final FilterLoader filterLoader = new FilterLoader();
            filterLoader.addFilter(serverAfterFilters);
            final FilterResponse filterResponse = filterLoader.doFilter(rpcResponseFilterData);
            if (!filterResponse.getResult()) {
                throw filterResponse.getException();
            }
        }

        responseRpcProtocol.setBody(response);
        channelHandlerContext.writeAndFlush(responseRpcProtocol);
    }
}
