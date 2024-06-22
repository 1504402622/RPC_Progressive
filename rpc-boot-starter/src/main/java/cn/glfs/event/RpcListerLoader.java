package cn.glfs.event;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RpcListerLoader {

    private static ExecutorService eventThreadPool = Executors.newFixedThreadPool(2);


    private static List<IRpcLister> rpcListerList = new ArrayList<>();

    public void init(){
        registerLister(new AddRpcLister());
        registerLister(new DestroyRpcLister());
        registerLister(new UpdateRpcLister());
    }
    public static void registerLister(IRpcLister rpcLister){
        rpcListerList.add(rpcLister);
    }

    public static void sendEvent(RpcEventData eventData){
        if (eventData == null){
            return;
        }
        if (!rpcListerList.isEmpty()){
            for (IRpcLister iRpcLister : rpcListerList) {
                // 获取泛型类型的参数是否与这个发来的事件的类型相同则传递执行
                final Class<?> generics = getInterfaceGenerics(iRpcLister);
                if (eventData.getClass().equals(generics)){
                    eventThreadPool.execute(()->{
                        iRpcLister.exec(eventData);
                    });
                }
            }
        }

    }

    public static Class<?> getInterfaceGenerics(Object o) {
        // 获取对象 o 所属类实现的接口的泛型类型列表
        Type[] types = o.getClass().getGenericInterfaces();
        // 获取接口的第一个泛型类型参数IRpcLister<AddRpcEventData>就是AddRpcEventData
        ParameterizedType parameterizedType = (ParameterizedType) types[0];
        // 获取类型参数的实际类型
        Type type = parameterizedType.getActualTypeArguments()[0];
        // 如果泛型类型参数是 Class 类型，返回该类型
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        // 否则返回 null
        return null;
    }
}
