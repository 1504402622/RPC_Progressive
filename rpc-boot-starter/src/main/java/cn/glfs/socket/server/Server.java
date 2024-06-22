package cn.glfs.socket.server;

import cn.glfs.config.Properties;
import cn.glfs.filter.FilterFactory;
import cn.glfs.invoke.InvokerFactory;
import cn.glfs.register.RegistryFactory;
import cn.glfs.socket.codec.RpcDecoder;
import cn.glfs.socket.codec.RpcEncoder;
import cn.glfs.socket.serialization.SerializationFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;


public class Server {

    private Integer port;

    private ServerBootstrap bootstrap;

    public Server(Integer port) {
        this.port = port;
    }

    public void run() throws Exception {
        // 创建两个事件循环组，bossGroup 用于处理连接请求，workerGroup 用于处理业务逻辑
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建 ServerBootstrap 实例
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    // 指定使用 NioServerSocketChannel 类来接受新的连接
                    .channel(NioServerSocketChannel.class)
                    // 设置子处理器，每当有新连接进来时会调用该初始化器来初始化该连接的处理器链
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 为新连接的 SocketChannel 添加编码器、解码器和业务处理器
                            ch.pipeline().addLast(new RpcEncoder());
                            ch.pipeline().addLast(new RpcDecoder());
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    })
                    // 设置一些 TCP 参数，如最大连接数（backlog）和保持连接状态
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            if (port == null) {
                // 绑定到随机端口
                int randomPort = generateRandomPort();
                Properties.setPort(randomPort);
                bootstrap.bind(randomPort).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            // 获取绑定成功的 Channel，并设置端口号到 Properties 中
                            Channel channel = channelFuture.channel();
                            InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();
                            Properties.setPort(localAddress.getPort());
                        }
                    }
                }).sync().channel().closeFuture().sync();
            } else {
                // 如果端口号已经指定，则绑定到指定的端口
                bootstrap.bind(port).sync().channel().closeFuture().sync();
            }
        } finally {
            // 优雅地关闭两个 EventLoopGroup
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void init() throws IOException, ClassNotFoundException {
        RegistryFactory.init();
        FilterFactory.initServer();
        InvokerFactory.init();
        SerializationFactory.init();
    }

    // 端口号随机生成
    public static int generateRandomPort() {
        // 端口号范围一般是 0 到 65535
        int minPort = 1024; // 避开系统保留端口
        int maxPort = 65535;

        Random random = new Random();
        return random.nextInt(maxPort - minPort + 1) + minPort;
    }
}
