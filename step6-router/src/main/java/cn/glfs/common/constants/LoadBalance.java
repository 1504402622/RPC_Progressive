package cn.glfs.common.constants;

public enum LoadBalance {
    // 轮询方式
    Round("round");


    public String name;
    LoadBalance(String type){
        this.name = type;
    }


}
