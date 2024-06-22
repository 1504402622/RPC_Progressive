package cn.glfs.common.constants;

public enum RpcProxy {
    CG_LIB("cglib");


    public String name;
    RpcProxy(String type){
        this.name = type;
    }

    public static RpcProxy get(String type){
        for (RpcProxy value : values()) {
            if (value.name.equals(type)) {
                return value;
            }
        }
        return null;
    }
}