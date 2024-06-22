package cn.glfs.socket.server;

import cn.glfs.annotation.RpcService;
import cn.glfs.common.Cache;
import cn.glfs.common.URL;
import cn.glfs.common.constants.Register;
import cn.glfs.filter.Filter;
import cn.glfs.filter.FilterData;
import cn.glfs.filter.FilterFactory;
import cn.glfs.filter.FilterResponse;
import cn.glfs.register.RegistryFactory;
import cn.glfs.register.RegistryService;
import cn.glfs.service.HelloService;
import cn.glfs.socket.codec.RpcDecoder;
import cn.glfs.socket.codec.RpcEncoder;
import cn.glfs.socket.codec.RpcRequest;
import cn.glfs.utils.ServiceNameBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {
    private Logger logger = LoggerFactory.getLogger(Server.class);

    private String host;
    private final int port;

    private ServerBootstrap bootstrap;

    public Server(int port) {
        this.port = port;
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        host = inetAddress.getHostAddress();
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
                            ch.pipeline().addLast(new ServerHandler());
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

    //服务注册
    public void registerBean(Class clazz) throws Exception {
        final URL url = new URL(host, port);
        //  Class 类中的方法，用于判断该类是否标记了指定的注解。
        if(!clazz.isAnnotationPresent(RpcService.class)){
            throw new Exception(clazz.getName()+"没有注解 RpcService");
        }
        // 获取类信息并为注解信息强转赋值
        final RpcService rpcService = (RpcService) clazz.getAnnotation(RpcService.class);
        // 获取类实现的第一个接口的名称
        String serviceName = clazz.getInterfaces()[0].getName();

        // 如果rpcService的serviceInterface不是void.class，则使用rpcService的serviceInterface的名称作为serviceName
        if(!(rpcService.serviceInterface().equals(void.class))){
            serviceName = rpcService.serviceInterface().getName();
        }

        // 设置url的serviceName和version
        url.setServiceName(serviceName);
        url.setVersion(rpcService.version());

        // 获取Zookeeper注册中心服务
        final RegistryService registryService = RegistryFactory.get(Register.ZOOKEEPER);

        // 将服务信息注册到注册中心
        registryService.register(url);

        // 构建服务的key，并将服务实例放入缓存
        final String key = ServiceNameBuilder.buildServiceKey(serviceName, rpcService.version());
        Cache.SERVICE_MAP.put(key, clazz.newInstance());
    }


    public static void main(String[] args) throws Exception {
        final Server server = new Server(8081);
        FilterFactory.registerServerBeforeFilter(new ServerTokenFilter());
        FilterFactory.registerServerAfterFilter(new Filter() {
            @Override
            public FilterResponse doFilter(FilterData filterData) {
                System.out.println("服务端后置拦截器");
                return new FilterResponse(true,null);
            }
        });

        // 1.将该服务的url即  rootPath/privider/cn.glfs.service.IHelloService/version/ip:port注册到zookeeper
        // 2.将(第一个接口serviceName$version,instance)注册到map集合中,因为客户端传过来的的就应该是该接口的ServiceName，然后通过相同方法名,并对instance传参调用达到接口方法信息-》（接口信息，实例） -》实例方法信息-》实例方法调用
        server.registerBean(HelloService.class);
        server.run();
    }
}
