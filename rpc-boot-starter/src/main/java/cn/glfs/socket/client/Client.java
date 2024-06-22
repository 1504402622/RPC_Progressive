package cn.glfs.socket.client;


import cn.glfs.common.Cache;
import cn.glfs.common.Host;
import cn.glfs.common.URL;
import cn.glfs.common.constants.Register;
import cn.glfs.event.RpcListerLoader;
import cn.glfs.filter.FilterFactory;
import cn.glfs.proxy.ProxyFactory;
import cn.glfs.register.RegistryFactory;
import cn.glfs.register.RegistryService;
import cn.glfs.router.LoadBalancerFactory;
import cn.glfs.socket.codec.RpcDecoder;
import cn.glfs.socket.codec.RpcEncoder;
import cn.glfs.tolerant.FaultTolerantFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.util.List;


public class Client {

    private  Bootstrap bootstrap;
    private  EventLoopGroup eventLoopGroup;

    public void run(){
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                // 当设置为true时，TCP会在一定时间（操作系统默认一般为2小时）内没有数据交互时，发送一个探测包（Keep-Alive包）给对方，以确认连接是否仍然可用。
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

        Cache.BOOT_STRAP = bootstrap;
    }

}
