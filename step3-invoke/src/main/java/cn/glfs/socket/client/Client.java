package cn.glfs.socket.client;

import cn.glfs.common.RpcFuture;
import cn.glfs.common.RpcRequestHolder;
import cn.glfs.common.constants.MsgType;
import cn.glfs.common.constants.ProtocolConstants;
import cn.glfs.common.constants.RpcSerialization;
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

    public Client(String host,Integer port) throws InterruptedException {
        this.port = port;
        this.host = host;
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);

        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new ClientHandler());
                    }
                });
        // 尝试连接到指定的主机和端口
        channelFuture = bootstrap.connect(host,port).sync();
    }

    public void sendRequest(Object o){
        channelFuture.channel().writeAndFlush(o);
    }

    public static void main(String[] args) throws InterruptedException, NoSuchMethodException, ExecutionException, TimeoutException {
        final Client client = new Client("127.0.0.1", 8081);
        final RpcProtocol rpcProtocol = new RpcProtocol();
        // 构建消息头
        MsgHeader header = new MsgHeader();
        long requestId = RpcRequestHolder.getRequestId();
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
        final Class<HelloService> aClass = HelloService.class;
        rpcRequest.setClassName(aClass.getName());
        // 在aClass类中查找名为hello且参数为String类型的方法
        final Method method = aClass.getMethod("hello", String.class);
        rpcRequest.setMethodCode(method.hashCode());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setServiceVersion("1.0");
        rpcRequest.setParameterTypes(method.getParameterTypes()[0]);
        rpcRequest.setParameter("glfs~");
        rpcProtocol.setBody(rpcRequest);

        client.sendRequest(rpcProtocol);
        RpcFuture<RpcResponse> future = new RpcFuture(new DefaultPromise(new DefaultEventLoop()),2000);
        RpcRequestHolder.REQUEST_MAP.put(requestId,future);
        Object rpcResponse = future.getPromise().sync().get(future.getTimeout(), TimeUnit.MILLISECONDS);
        System.out.println(rpcResponse);
    }
}
