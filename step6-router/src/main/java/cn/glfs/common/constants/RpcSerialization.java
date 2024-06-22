package cn.glfs.common.constants;

/**
 * 序列化值对象
 */
public enum RpcSerialization {
    JSON("json"),
    JDK("jdk");
    public String name;
    RpcSerialization(String type){
        this.name = type;
    }
    public static RpcSerialization get(String type){
        // values()是一个Java中的枚举方法，用于返回枚举类型中所有枚举常量的数组
        for (RpcSerialization value : values()) {
            if (value.name.equals(type)) {
                return value;
            }
        }
        return null;
    }
}
