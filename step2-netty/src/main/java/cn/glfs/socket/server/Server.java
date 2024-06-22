package cn.glfs.socket.server;

import cn.glfs.socket.codec.RpcDecoder;
import cn.glfs.socket.codec.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private Logger logger = LoggerFactory.getLogger(Server.class);
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RpcEncoder());
                            ch.pipeline().addLast(new RpcDecoder());
                            ch.pipeline().addLast(new SimpleChatServerHandler());
                        }
                    })
                    // 这行代码设置了服务器端SocketChannel的SO_BACKLOG参数为128。SO_BACKLOG参数是指定服务器端接受连接的队列大小，即服务器端可以接受的客户端连接请求的最大数量。当服务器端处理连接请求的速度比较慢时，超过这个数量的连接请求会被拒绝。
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // SO_KEEPALIVE参数是用来设置TCP连接的保活机制，即在一定时间内没有数据传输时，服务器端会发送保活探测包给客户端，以检测连接是否仍然有效
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            b.bind(port).sync().channel().closeFuture().sync();
            logger.info("rpc server 启动: ",port);
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        new Server(8081).run();
    }
}
