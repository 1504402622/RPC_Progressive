package cn.glfs.common.constants;


public enum LoadBalance {
    Round("round");


    public String name;
    LoadBalance(String type){
        this.name = type;
    }


}
