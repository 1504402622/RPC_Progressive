package cn.glfs.socket.server;

import cn.glfs.common.constants.MsgType;
import cn.glfs.config.Properties;
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

        final RpcRequest rpcRequest = rpcProtocol.getBody();
        final RpcResponse response = new RpcResponse();
        final RpcProtocol<RpcResponse> resRpcProtocol = new RpcProtocol();
        final MsgHeader header = rpcProtocol.getHeader();
        header.setMsgType((byte) MsgType.RESPONSE.ordinal());
        resRpcProtocol.setHeader(header);
        final Invoker invoker = InvokerFactory.get(Properties.getInvoke());
        try {
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
            // 执行
            final Object data = invoker.invoke(new Invocation(rpcRequest));
            response.setData(data);
        }catch (Exception e){
            response.setException(e);
        }finally {
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
        }
        resRpcProtocol.setBody(response);
        channelHandlerContext.writeAndFlush(resRpcProtocol);

    }
}