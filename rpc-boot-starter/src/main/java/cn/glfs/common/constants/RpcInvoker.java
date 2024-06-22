package cn.glfs.common.constants;


public enum RpcInvoker {
    REFLECTION("reflection");

    public String name;
    RpcInvoker(String type){
        this.name = type;
    }


}
