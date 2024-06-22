package cn.glfs.proxy.cglib;

import cn.glfs.annotation.RpcReference;
import cn.glfs.common.*;
import cn.glfs.common.constants.FaultTolerant;
import cn.glfs.common.constants.LoadBalance;
import cn.glfs.common.constants.MsgType;
import cn.glfs.common.constants.ProtocolConstants;
import cn.glfs.config.Properties;
import cn.glfs.filter.*;
import cn.glfs.register.RegistryFactory;
import cn.glfs.router.LoadBalancer;
import cn.glfs.router.LoadBalancerFactory;
import cn.glfs.socket.codec.MsgHeader;
import cn.glfs.socket.codec.RpcProtocol;
import cn.glfs.socket.codec.RpcRequest;
import cn.glfs.socket.codec.RpcResponse;
import cn.glfs.tolerant.FaultContext;
import cn.glfs.tolerant.FaultTolerantFactory;
import cn.glfs.tolerant.FaultTolerantStrategy;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoop;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.concurrent.DefaultPromise;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CgLibProxy implements MethodInterceptor {


    private final String serviceName;

    private final String version;

    private final FaultTolerant faultTolerant;

    private final long time;

    private final TimeUnit timeUnit;

    private final LoadBalance loadBalance;

    public CgLibProxy(Class claz, RpcReference rpcReference) {
        this.serviceName = claz.getName();
        this.version = rpcReference.version();
        this.faultTolerant = rpcReference.faultTolerant();
        this.time = rpcReference.time();
        this.timeUnit = rpcReference.timeUnit();
        this.loadBalance = rpcReference.loadBalance();
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
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
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodCode(method.hashCode());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setServiceVersion(version);
        if (null!=objects && objects.length >0){
            rpcRequest.setParameterTypes(objects[0].getClass());
            rpcRequest.setParameter(objects[0]);
        }

        rpcProtocol.setBody(rpcRequest);
        final List<URL> urls = RegistryFactory.get(Properties.getRegister().getName()).discoveries(serviceName, version);
        if (urls.isEmpty()){
            throw new Exception("无服务可用:"+serviceName);
        }
        final LoadBalancer loadBalancer = LoadBalancerFactory.get(loadBalance);
        final URL url = loadBalancer.select(urls);

        final ChannelFuture channelFuture = Cache.CHANNEL_FUTURE_MAP.get(new Host(url.getIp(),url.getPort()));
        final List<Filter> clientBeforeFilters = FilterFactory.getClientBeforeFilters();
        if (!clientBeforeFilters.isEmpty()){
            final FilterData<RpcRequest> rpcRequestFilterData = new FilterData<>(rpcRequest);
            final FilterLoader filterLoader = new FilterLoader();
            filterLoader.addFilter(clientBeforeFilters);
            final FilterResponse filterResponse = filterLoader.doFilter(rpcRequestFilterData);
            if (!filterResponse.getResult()) {
                throw filterResponse.getException();
            }
        }

        // 发送
        channelFuture.channel().writeAndFlush(rpcProtocol);
        RpcFuture<RpcResponse> future = new RpcFuture(new DefaultPromise(new DefaultEventLoop()), time);
        RpcRequestHolder.REQUEST_MAP.put(requestId, future);
        RpcResponse rpcResponse = future.getPromise().sync().get(future.getTimeout(), timeUnit);

        final List<Filter> clientAfterFilters = FilterFactory.getClientAfterFilters();
        if (!clientBeforeFilters.isEmpty()){
            final FilterData<RpcResponse> rpcResponseFilterData = new FilterData<>(rpcResponse);
            final FilterLoader filterLoader = new FilterLoader();
            filterLoader.addFilter(clientAfterFilters);
            final FilterResponse filterResponse = filterLoader.doFilter(rpcResponseFilterData);
            if (!filterResponse.getResult()) {
                throw filterResponse.getException();
            }
        }
        // 发生异常
        if (rpcResponse.getException()!=null){
            rpcResponse.getException().printStackTrace();
            final FaultContext faultContext = new FaultContext(url,urls,rpcProtocol,requestId,rpcResponse.getException());
            final FaultTolerantStrategy faultTolerantStrategy = FaultTolerantFactory.get(faultTolerant);
            return faultTolerantStrategy.handler(faultContext);
        }
        return rpcResponse.getData();
    }

}
