package cn.glfs.socket.client;

import cn.glfs.common.*;
import cn.glfs.common.constants.MsgType;
import cn.glfs.common.constants.ProtocolConstants;
import cn.glfs.common.constants.RpcProxy;
import cn.glfs.common.constants.RpcSerialization;
import cn.glfs.proxy.IProxy;
import cn.glfs.proxy.ProxyFactory;
import cn.glfs.service.HelloService;
import cn.glfs.socket.codec.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Client {
    private Logger logger = LoggerFactory.getLogger(Client.class);
    private final String host;
    private final Integer port;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private ChannelFuture channelFuture;
    public Client(String host, Integer port) throws InterruptedException {
        this.host = host;
        this.port = port;

        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new ClientHandler());
                    }
                });

    }



    public void registerBean(String serviceName){
        final URL url = new URL(this.host, port);
        Cache.services.put(new ServiceName(serviceName),url);
        // 连接请求会在调用ChannelFuture对象的sync()或者addListener()方法时才会真正发起
        // 调用sync()方法会阻塞当前线程，直到连接建立完成或者连接失败。这个方法会返回一个Channel对象，表示连接建立成功后的通道。在调用sync()方法时，Netty会自动分配一个线程来处理连接请求，但是当前线程会被阻塞直到连接建立完成。
        channelFuture = bootstrap.connect(host,port);
        Cache.channelFutureMap.put(url,channelFuture);
    }

    public static void main(String[] args) throws Exception {
        final Client client = new Client("127.0.0.1", 8081);
        client.registerBean(HelloService.class.getName());
        final IProxy iproxy = ProxyFactory.get(RpcProxy.CG_LIB);
        final HelloService proxy = iproxy.getProxy(HelloService.class);
        System.out.println(proxy.hello("xixi"));

    }
}
