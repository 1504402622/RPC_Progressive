package cn.glfs.event;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcListerLoader {
    // 创建一个固定大小为2的线程池，用于执行事件监听器的处理逻辑
    private static ExecutorService eventThreadPool = Executors.newFixedThreadPool(2);
    // 创建一个存储IRpcLister接口实现类的列表,用于存储已注册的事件监听器
    private static List<IRpcLister> rpcListerList = new ArrayList<>();
    // 初始化方法，用于注册默认的事件监听器
    public void init(){
        registerLister(new AddRpcLister());
        registerLister(new DestroyRpcLister());
        registerLister(new UpdateRpcLister());
    }
    // 静态方法，用于注册事件监听器到rpcListerList列表中
    public static void registerLister(IRpcLister rpcLister){
        rpcListerList.add(rpcLister);
    }

    // 静态方法，用于发送事件给已注册的监听器
    public static void sendEvent(RpcEventData eventData){
        if(eventData == null){
            return;
        }
        if(!rpcListerList.isEmpty()){
            for(IRpcLister iRpcLister:rpcListerList){
                // 获取接口上的泛型
                final Class<?> generics = getInterfaceGenerics(iRpcLister);
                if(eventData.getClass().equals(generics)){
                    // 在线程池中执行监听器的处理逻辑
                    eventThreadPool.execute(()->{
                        iRpcLister.exec(eventData);
                    });
                }
            }
        }
    }

    // 静态方法,用于获取接口上的泛型类型
    public static Class<?> getInterfaceGenerics(Object o){
        // 获取对象 o 所实现的接口的泛型信息
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) types[0];
        // 获取接口中第一个泛型参数的类型信息
        Type type = parameterizedType.getActualTypeArguments()[0];
        // 判断获取到的类型信息是否是 Class 类型，如果是则将其转换为 Class 类型并返回，否则返回 null
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }
}
