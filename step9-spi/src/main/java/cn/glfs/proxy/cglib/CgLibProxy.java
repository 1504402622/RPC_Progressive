package cn.glfs.proxy.cglib;

import cn.glfs.annotation.RpcReference;
import cn.glfs.common.Cache;
import cn.glfs.common.RpcFuture;
import cn.glfs.common.RpcRequestHolder;
import cn.glfs.common.URL;
import cn.glfs.common.constants.*;
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
import io.netty.util.concurrent.DefaultPromise;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CgLibPro xy implements MethodInterceptor {


    private final String version;
    private final String serviceName;
    private final FaultTolerant faultTolerant;
    private final long time;

    private final TimeUnit timeUnit;
    public CgLibProxy(Class clazz){
        this.serviceName = clazz.getName();
        final RpcReference rpcService = (RpcReference) clazz.getAnnotation(RpcReference.class);
        version = rpcService.version();
        faultTolerant = rpcService.faultTolerant();
        time = rpcService.time();
        timeUnit = rpcService.timeUnit();
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
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodCode(method.hashCode());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setServiceVersion("1.0");
        // 先只对一个参数的方法进行处理
        if (null!=objects && objects.length >0){
            rpcRequest.setParameterTypes(objects[0].getClass());
            rpcRequest.setParameter(objects[0]);
        }
        rpcProtocol.setBody(rpcRequest);
        final List<URL> urls = RegistryFactory.get(Register.ZOOKEEPER).discoveries(serviceName, version);
        if (urls.isEmpty()){
            throw new Exception("无服务可用:"+serviceName);
        }
        // 轮询负载均衡
        final LoadBalancer loadBalancer = LoadBalancerFactory.get(LoadBalance.Round);
        final URL url = loadBalancer.select(urls);
        final ChannelFuture channelFuture = Cache.CHANNEL_FUTURE_MAP.get(new Host(url.getIp(),url.getPort()));

        // 拦截器（发起请求前）
        List<Filter> clientBeforeFilters = FilterFactory.getClientBeforeFilters();
        if(!clientBeforeFilters.isEmpty()){
            final FilterData<RpcRequest> rpcRequestFilterData = new FilterData<>(rpcRequest);
            final FilterLoader filterLoader = new FilterLoader();
            filterLoader.addFilter(clientBeforeFilters);
            final FilterResponse filterResponse = filterLoader.doFilter(rpcRequestFilterData);
            if (!filterResponse.getResult()) {
                throw filterResponse.getException();
            }
        }


        channelFuture.channel().writeAndFlush(rpcProtocol);
        RpcFuture<RpcResponse> future = new RpcFuture(new DefaultPromise(new DefaultEventLoop()), time);
        // 指定在调用远程服务时的超时时间
        RpcRequestHolder.REQUEST_MAP.put(requestId, future);
        RpcResponse rpcResponse = future.getPromise().sync().get(future.getTimeout(), timeUnit);

        // 拦截器（返回响应后）
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

        if (rpcResponse.getException()!=null){
            rpcResponse.getException().printStackTrace();
            final FaultContext faultContext = new FaultContext(url,urls,rpcProtocol,requestId,rpcResponse.getException());
            final FaultTolerantStrategy faultTolerantStrategy = FaultTolerantFactory.get(faultTolerant);
            return faultTolerantStrategy.handler(faultContext);
        }

        return rpcResponse.getData();
    }
}
