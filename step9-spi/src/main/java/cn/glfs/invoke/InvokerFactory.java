package cn.glfs.invoke;

import cn.glfs.common.constants.RpcInvoker;
import cn.glfs.spi.ExtensionLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InvokerFactory {
    public static Invoker get(RpcInvoker rpcInvoker){
        return ExtensionLoader.getInstance().get(rpcInvoker.name);
    }

    public static Invoker get(String name){
        return ExtensionLoader.getInstance().get(name);
    }

    public static void init() throws IOException, ClassNotFoundException {
        ExtensionLoader.getInstance().loadExtension(Invoker.class);
    }
}
