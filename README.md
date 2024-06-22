

# 一.前言

## 1.什么是中间件

**中间件**：是介于操作系统和应用软件之间，为应用软件提供服务功能的软件，有消息中间件，通信中间件，应用服务器等。**由于介于两种软件之间，所以，称为中间件。**



## 2.**为什么使用中间件**

使用中间件来自于中间件本身的价值能力，具体的说中间件屏蔽了底层操作系统的复杂性，让开发工程师可以把更多的专注力放在业务系统的逻辑和流程实现上，也就是说**框架和中间件的存在，是了让程序员只关心业务开发**



## **3.什么是RPC？**

RPC就是一种通信中间件，RPC（Remote Procedure Call）远程过程调用，简言之就是像调用本地方法一样调用远程服务。目前外界使用较多的有gRPC、Dubbo、Spring Cloud等。



## 4.什么是Spring-Boot-Starter

在这个项目中,我们之后要整合到SpringBoot项目中,供provider和consumer使用

SpringBoot Starter的功能将一个第三方的组件的核心类的Bean整合到Spring容器中实现自动装配，简化依赖管理，实现简化开发的达到让客户端只需要关心业务逻辑目的

官方的 starter 和第三方的 starter 组件，最大的区别在于命名上。 官方维护的 starter 的以 spring-boot-starter 开头的前缀。 第三方维护的 starter 是以 spring-boot-starter 结尾的后缀 这也是一种约定优于配置的体现。

![image-20240613195927184](https://gitee.com/w1504402622/ty_img/raw/master/img/202406131959216.png)



**该项目以JPI技术落地实现Starter**





# 二.RPC框架要素

一款分布式RPC框架离不开三个基本要素：

-   服务提供方 Serivce Provider
-   服务消费方 Servce Consumer
-   注册中心 Registery

围绕上面三个基本要素可以进一步扩展服务路由、负载均衡、序列化协议、通信协议、拦截器、等等。

1.  **注册中心**

    主要是用来完成服务注册、发现、订阅的工作。虽然服务调用是服务消费方直接发向服务提供方的，但是现在服务都是集群部署，服务的提供者数量也是动态变化的，所以服务的地址也就无法预先确定。因此如何发现这些服务就需要一个统一注册中心来承载。

2.  **服务提供方（RPC服务端）**

    其需要对外提供服务接口，它需要在应用启动时连接注册中心，将服务名及其服务元数据发往注册中心。同时需要提供服务服务下线的机制。需要维护服务名和真正服务地址映射。服务端还需要启动Socket服务监听客户端请求。

3.  **服务消费方（RPC客户端）**

    客户端需要有从注册中心获取服务的基本能力，它需要在应用启动时，扫描依赖的RPC服务，并为其生成代理调用对象，同时从注册中心拉取服务元数据存入本地缓存，然后发起监听各服务的变动做到及时更新缓存。在发起服务调用时，通过代理调用对象，从本地缓存中获取服务地址列表，然后选择一种负载均衡策略筛选出一个目标地址发起调用。调用时会对请求数据进行序列化，并采用一种约定的通信协议进行socket通信。





# **三.技术选型**

## 1.注册中心

目前成熟的注册中心有Zookeeper，Nacos，Consul，Eureka，它们的主要比较如下：

![img](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220816606.png)

这里采用Zookeeper



## 2.IO通信框架

本实现采用Netty作为底层通信框架，Netty是一个高性能事件驱动型的基于多路复用模型IO框架。



## 3.通信协议

TCP通信过程中会根据TCP缓冲区的实际情况进行包的划分，所以在业务上认为一个完整的包可能会被TCP拆分成多个包进行发送，也有可能把多个小的包封装成一个大的数据包发送，这就是所谓的TCP粘包和半包问题。所以需要对发送的数据包封装到一种通信协议里。

业界的主流协议的解决方案可以归纳如下：

1.  消息定长，例如每个报文的大小为固定长度100字节，如果不够用空格补足。
2.  在包尾特殊结束符进行分割。
3.  **将消息分为消息头和消息体，消息头中包含表示消息总长度（或者消息体长度）的字段。**

很明显1，2都有些局限性，本实现采用方案3，**自定义协议**



## 4.序列化协议

常见的协议有jdk、json、Kryo及Hessian。项目对这四种序列化方式都进行实现，

1.  JDK 序列化：

    -   默认的 Java 序列化机制，使用 `java.io.Serializable` 接口实现对象的序列化和反序列化。
    -   可以序列化任何实现 `Serializable` 接口的对象，无需额外的配置。
    -   序列化后的结果包含类的元数据和字段的类型信息，导致序列化对象的字节大小较大。
    -   对于跨语言兼容性和序列化效率方面存在一定的局限性。

    

2.  Hessian 序列化：

    -   Hessian 是一种基于二进制的序列化方式，可以将对象序列化为字节数组，也可以反序列化为对象。
    -   序列化后的字节流较小，因为它不包含类的元数据和字段的类型信息，适合网络传输和分布式系统。
    -   Hessian 序列化效率较高，而且支持跨语言的操作。

    

3.  Kryo 序列化：

    -   Kryo 是高性能的 Java 序列化库，具有较高的序列化和反序列化速度。
    -   序列化后的结果非常紧凑，因为它使用类注册机制来存储对象，不需要包含类信息。
    -   Kryo 需要用户手动注册需要序列化的类，以提高性能和安全性，对于复杂对象结构需要一些额外的配置。

    

4.  Fastjson 序列化：

    -   Fastjson 是一种快速和灵活的 JSON 库，可以将 Java 对象转换为 JSON 字符串，也可以将 JSON 字符串转换为 Java 对象。
    -   Fastjson 序列化速度较快，但序列化后的结果通常比二进制序列化的字节数大。
    -   不仅支持标准的序列化和反序列化操作，还提供了诸如序列化过滤器、自定义序列化器和反序列化器等功能。



## 5.客户端动态代理

每次请求封装如果总让客户端去操作那就太沉重了，使用动态代理去操作这些冗余步骤大大减少客户端代码量，动态代理可用CgLib动态代理和JDK动态代理，因为前期需要通过service实现类进行简单测试，所以这里选型CgLib动态代理



## 6.JPI

JPI是一种可拔插设计思想的体现，不是具体落地的实现，能够用提高程序的拓展性，SPI不限于语言限制，是实现Spring Starter的基础





# 四.整体架构

![image-20240622084802513](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220848577.png)



下面就来看看实现吧



## 1.通信相关

### （1）通信协议

![image-20240622090722072](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220907147.png)

**请求头：**

-   第一个表示魔数，我定义为0x10。
-   第二个表示服务版本，以便获取服务的不同版本。
-   第三个表示消息类型，REQUEST请求,RESPONSE响应,HEARTBEAT心跳（测试确认连接是否可用）;

-   第四个表示消息状态，SUCCESS成功，FAILED失败
-   第五个表示请求id，以便使用promise发起异步请求之后找到该对象删除缓存，并给消息附加一个唯一id
-   第六个表示序列化方式的长度，因为不同序列化方式的会占用不同字节，标注该长度读取的时候就能知道需要再往后读几个字节

*   第七个表示请求头数据长度



**请求体：**

**请求类型的请求体：**服务相关信息，如版本号，接口名/类名，方法名，方法的HashCode，参数类型，参数

**响应类型的请求头：**Object对象、异常信息



### （2）编解码器

**编码器**

```java
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        // 获取消息头类型
        MsgHeader header = msg.getHeader();
        // 写入魔数(安全校验，可以参考java中的CAFEBABE)
        byteBuf.writeShort(header.getMagic());
        // 写入版本号
        byteBuf.writeByte(header.getVersion());
        // 写入消息类型(接收放根据不同的消息类型进行不同的处理方式)
        byteBuf.writeByte(header.getMsgType());
        // 写入状态
        byteBuf.writeByte(header.getStatus());
        // 写入请求id(请求id可以用于记录异步回调标识,具体需要回调给哪个请求)
        byteBuf.writeLong(header.getRequestId());
        RpcSerialization rpcSerialization = SerializationFactory.get(Properties.getSerialization());
        byte[] data = rpcSerialization.serialize(msg.getBody());
        // 写入数据长度(接收方根据数据长度读取数据内容)
        byteBuf.writeInt(data.length);
        // 写入数据
        byteBuf.writeBytes(data);
    }
}
```



**解码器**

```java
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

        // 如果可读字节数少于协议头长度，说明还没有接收完整个协议头，直接返回
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        // 标记当前读取位置，便于后面回退
        in.markReaderIndex();

        // 读取魔数字段
        short magic = in.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }
        // 读取版本字段
        byte version = in.readByte();
        // 读取消息类型
        byte msgType = in.readByte();
        // 读取响应状态
        byte status = in.readByte();
        // 读取请求 ID
        long requestId = in.readLong();
        // 读取消息体长度
        int dataLength = in.readInt();
        // 如果可读字节数小于消息体长度，说明还没有接收完整个消息体，回退并返回
        if (in.readableBytes() < dataLength) {
            // 回退标记位置
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        // 读取数据
        in.readBytes(data);

        // 处理消息的类型
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }

        // 构建消息头
        MsgHeader header = new MsgHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setMsgLen(dataLength);

        RpcSerialization rpcSerialization = SerializationFactory.get(Properties.getSerialization());
        RpcProtocol protocol = new RpcProtocol<>();
        protocol.setHeader(header);
        switch (msgTypeEnum) {
            // 请求消息
            case REQUEST:
                RpcRequest request = rpcSerialization.deserialize(data, RpcRequest.class);
                protocol.setBody(request);
                break;
            // 响应消息
            case RESPONSE:
                RpcResponse response = rpcSerialization.deserialize(data, RpcResponse.class);
                protocol.setBody(response);
                break;
        }
        out.add(protocol);
    }
}
```







### （3）数据交互流程



![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220923620.png)





## 2.客户端代理模块

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220925901.png)





## 3.序列化/反序化层

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220926046.png)





## 4.服务端反射调用模块

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220927116.png)



## 5.Zookeeper注册中心

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220928630.png)



## 6.路由（负载均衡）层

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220929640.png)





## 7.容错层

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220930824.png)





## 8.拦截器层

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220931102.png)





## 9.JPI插拔控制层

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220932328.png)



## 10.SpringBoot设计

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220932712.png)

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406220949004.png)



## 11.总时间流程

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406221533716.png)

![rpc.drawio](https://gitee.com/w1504402622/ty_img/raw/master/img/202406221533388.png)







# 五.测试

## 1.功能测试

（1）开启Zookeeper

（2）设置接口，priovider提供实现类

（3）开启两个provider

（4）开启consumer

![image-20240622154131177](https://gitee.com/w1504402622/ty_img/raw/master/img/202406221541239.png)

interface中的接口

```java
public interface HelloService {
    Object hello(String arg);
}
```

consumer

```java
@RestController
@RequestMapping("/test")
public class Web {

    @RpcReference
    HelloService helloService;

    @GetMapping("/set")
    public Object hello(String arg){
        return helloService.hello(" hello~");
    }
}
```

provider1

```java
@Component
@RpcService
public class TestService implements HelloService{
    @Override
    public Object hello(String arg) {
        return arg + "provider1";
    }
}
```

provider2

```java
@Component
@RpcService
public class TestService implements HelloService{
    @Override
    public Object hello(String arg) {
        return arg + "provider2";
    }
}
```

第一次请求

![](https://gitee.com/w1504402622/ty_img/raw/master/img/202406221543107.png)

第二次请求

![image-20240622154341945](https://gitee.com/w1504402622/ty_img/raw/master/img/202406221543040.png)





