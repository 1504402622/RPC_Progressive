package cn.glfs.register;

import cn.glfs.common.Cache;
import cn.glfs.common.URL;
import cn.glfs.event.*;
import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.*;

public class CuratorZookeeperRegistry extends AbstractZookeeperRegistry  {
    // 连接失败等待重试时间
    public static final int BASE_SLEEP_TIME_MS = 1000;
    // 重试次数
    public static final int MAX_RETRIES = 3;
    // 跟路径
    public static final String ROOT_PATH = "/glfs_rpc";
    public static final String PROVIDER = "/provider";

    private final CuratorFramework client;
    /**
     * 启动zk
     * 首先创建了一个CuratorFramework客户端对象，通过CuratorFrameworkFactory.newClient()方法创建，传入注册中心地址registerAddr和一个ExponentialBackoffRetry对象作为参数。然后调用start()方法启动客户端。
     * @throws Exception
     */
    public CuratorZookeeperRegistry(String registerAddr) {
        client = CuratorFrameworkFactory.newClient(registerAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
    }

    /**
     *服务注册
     */
    public void register(URL url) throws Exception{

        // 如果根节点不存在,则创建根节点
        if(!existNode(ROOT_PATH)){
            client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT).forPath(ROOT_PATH,"".getBytes());
        }
        // 获取服务提供者数据节点路径 全路径+ip:port
        final String providerDataPath = getProviderDataPath(url);
        // 如果服务提供者数据节点已存在，则删除
        if (existNode(providerDataPath)) {
            deleteNode(providerDataPath);
        }


        /**
         * client.create(): 创建一个ZooKeeper节点的操作。
         * creatingParentContainersIfNeeded(): 如果指定的节点的父节点不存在，则自动创建父节点。
         * withMode(CreateMode.EPHEMERAL): 指定创建的节点为临时节点，即当客户端与ZooKeeper断开连接时，该节点会被自动删除。
         * forPath(providerDataPath, JSON.toJSONString(url).getBytes()): 在指定的路径providerDataPath下创建一个节点，并将url对象序列化为JSON字符串后存储在该节点中。
         */
        client.create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath(providerDataPath, JSON.toJSONString(url).getBytes());

    }

    /**
     * 取消注册服务
     */
    public void unRegister(URL url) throws Exception {
        // 删除服务提供者数据节点
        deleteNode(getProviderDataPath(url));
        super.unRegister(url);
    }


    /**
     * 发现服务
     */
    public List<URL> discoveries(String serviceName, String version) throws Exception {
        List<URL> urls = super.discoveries(serviceName, version);
        if (null == urls || urls.isEmpty()){
            // 如果本地缓存中没有数据，则从ZooKeeper中获取数据
            final List<String> strings = client.getChildren().forPath(getProviderPath(serviceName, version));
            if (!strings.isEmpty()) {
                urls = new ArrayList<>();
                for (String string : strings) {
                    final String[] split = string.split(":");
                    urls.add(new URL(split[0],Integer.parseInt(split[1])));
                }
            }
        }
        return urls;
    }

    /**
     * 订阅服务
     * 服务订阅是指客户端向ZooKeeper注册对某个特定服务或节点的监听，以便在该服务或节点发生变化时能够及时收到通知。通过服务订阅，客户端可以实时获取服务的状态变化，比如节点的创建、更新、删除等操作，从而能够及时做出相应的处理
     */
    @Override
    public void subscribe(URL url) throws Exception {

        // TODO 如果没有节点直接订阅会报错吧
        final String path = getProviderPath(url.getServiceName(), url.getVersion());
        Cache.SUBSCRIBE_SERVICE_LIST.add(url);
        this.watchNodeDataChange(path);
    }

    @Override
    public void unSubscribe(URL url) {
        // 取消订阅服务
    }

    /**
     * 监听节点数据
     */
    public void watchNodeDataChange(String path) throws Exception{
        // 创建一个PathChildrenCache对象cache，传入client（CuratorFramework客户端对象）、path（要监听的节点路径）和true（表示是否缓存节点数据）作为参数
        PathChildrenCache cache = new PathChildrenCache(client, path, true);
        // 启动PathChildrenCache，并指定启动模式为POST_INITIALIZED_EVENT，表示在初始化事件之后开始监听
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        // 添加一个PathChildrenCacheListener监听器，重写childEvent方法，该方法会在节点数据发生变化时被调用
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                final PathChildrenCacheEvent.Type type = event.getType();
                System.out.println("PathChildrenCache event: " + type);
                RpcEventData eventData = null;
                // 如果是CHILD_REMOVED类型，表示子节点被移除，获取移除节点的路径，并解析出对应的URL对象，然后创建一个DestroyEventData对象。
                if (type.equals(CHILD_REMOVED)){
                    String path = event.getData().getPath();
                    final URL url = parsePath(path);
                    eventData = new DestroyEventData(url);
                    // 如果是CHILD_UPDATED或CHILD_ADDED类型，表示子节点被更新或新增，获取节点路径，并通过client.getData().forPath(path)获取节点数据的字节数组，然后将字节数组解析为URL对象。根据事件类型创建相应的UpdateRpcEventData或AddRpcEventData对象。
                } else if ((type.equals(CHILD_UPDATED)) || type.equals(CHILD_ADDED)){
                    String path = event.getData().getPath();
                    byte[] bytes = client.getData().forPath(path);
                    Object o = JSON.parseObject(bytes, URL.class);
                    eventData = type.equals(CHILD_UPDATED) ? new UpdateRpcEventData(o) : new AddRpcEventData(o);
                }
                // 用于发送事件数据到一个名为RpcListerLoader的类或组件。这个类或组件负责处理和分发事件数据
                // 通过RpcListerLoader.sendEvent(eventData)方法发送相应的事件数据
                RpcListerLoader.sendEvent(eventData);
            }
        });
    }

    /**
     * 通过url获取服务对应全路径+ip:port
     */
    private String getProviderDataPath(URL url) {

        return ROOT_PATH+PROVIDER+"/"+url.getServiceName()+"/"+url.getVersion()+"/"+url.getIp()+":"+url.getPort();
    }

    /**
     * 通过url获取服务对应全路径
     */
    private String getProviderPath(URL url) {

        return ROOT_PATH+PROVIDER+"/"+url.getServiceName()+"/"+url.getVersion();
    }

    /**
     * 通过服务名获取服务对应全路径
     */
    private String getProviderPath(String serviceName,String version) {

        return ROOT_PATH+PROVIDER+"/"+serviceName+"/"+version;
    }

    /**
     * 解析单个服务路径获取该路径对应的URL
     */
    private URL parsePath(String path) throws Exception {
        final String[] split = path.split("/");
        String className = split[3];
        String version = split[4];
        final String[] split1 = split[5].split(":");
        String host = split1[0];
        String port = split1[1];
        final URL url = new URL();
        url.setServiceName(className);
        url.setVersion(version);
        url.setIp(host);
        url.setPort(Integer.parseInt(port));
        return url;
    }

    public boolean deleteNode(String path) {
        try {
            client.delete().forPath(path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean existNode(String path){
        try {
            Stat stat = client.checkExists().forPath(path);
            return stat != null;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
