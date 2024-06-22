package cn.glfs.proxy.cglib;

import cn.glfs.common.*;
import cn.glfs.common.constants.MsgType;
import cn.glfs.common.constants.ProtocolConstants;
import cn.glfs.common.constants.RpcSerialization;
import cn.glfs.socket.codec.MsgHeader;
import cn.glfs.socket.codec.RpcProtocol;
import cn.glfs.socket.codec.RpcRequest;
import cn.glfs.socket.codec.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class CgLibProxy implements MethodInterceptor {

    private final Object object;
    public CgLibProxy(Object o){
        this.object = o;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        final RpcProtocol rpcProtocol = new RpcProtocol();
        // 构建消息头
        MsgHeader header = new MsgHeader();
        Long requestId = RpcRequestHolder.getRequestId();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);

        final byte[] serialization = RpcSerialization.JSON.name.getBytes();
        header.setSerializationLen(serialization.length);
        header.setSerialization(serialization);
        header.setMsgType((byte) MsgType.REQUEST.ordinal());
        header.setStatus((byte) 0x1);
        rpcProtocol.setHeader(header);

        final RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName(object.getClass().getName());
        rpcRequest.setMethodCode(method.hashCode());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setServiceVersion("1.0");
        // 先只对一个参数的方法进行处理
        if (null!=objects && objects.length >0){
            rpcRequest.setParameterTypes(objects[0].getClass());
            rpcRequest.setParameter(objects[0]);
        }
        rpcProtocol.setBody(rpcRequest);
        final URL url = Cache.services.get(new ServiceName(object.getClass().getName()));
        final ChannelFuture channelFuture = Cache.channelFutureMap.get(url);

        channelFuture.channel().writeAndFlush(rpcProtocol);
        RpcFuture<RpcResponse> future = new RpcFuture(new DefaultPromise(new DefaultEventLoop()), 2000);

        RpcRequestHolder.REQUEST_MAP.put(requestId, future);
        RpcResponse rpcResponse = future.getPromise().sync().get(future.getTimeout(), TimeUnit.MILLISECONDS);
        if (rpcResponse.getException()!=null){
            throw rpcResponse.getException();
        }
        return rpcResponse.getData();
    }
}
