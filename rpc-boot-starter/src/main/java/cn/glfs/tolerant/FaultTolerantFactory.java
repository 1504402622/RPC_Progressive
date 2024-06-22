package cn.glfs.tolerant;

import cn.glfs.common.constants.FaultTolerant;
import cn.glfs.spi.ExtensionLoader;

import java.io.IOException;


public class FaultTolerantFactory {


    public static FaultTolerantStrategy get(FaultTolerant faultTolerant){
        final String name = faultTolerant.name;
        return ExtensionLoader.getInstance().get(name);
    }

    public static FaultTolerantStrategy get(String name){
        return ExtensionLoader.getInstance().get(name);
    }

    public static void init() throws IOException, ClassNotFoundException {
        final ExtensionLoader extensionLoader = ExtensionLoader.getInstance();
        extensionLoader.loadExtension(FaultTolerantStrategy.class);
    }
}
