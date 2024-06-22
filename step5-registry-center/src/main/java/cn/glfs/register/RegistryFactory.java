package cn.glfs.register;



import cn.glfs.common.constants.Register;
import java.util.HashMap;
import java.util.Map;

// 获取不同注册中心
public class RegistryFactory {
    private static Map<Register,RegistryService> registryServiceMap = new HashMap<Register, RegistryService>();

    static{
        registryServiceMap.put(Register.ZOOKEEPER,new CuratorZookeeperRegistry("127.0.0.1:2181"));
    }
    public static RegistryService get(Register register){
        return registryServiceMap.get(register);
    }
}
