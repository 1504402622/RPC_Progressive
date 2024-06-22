package cn.glfs.socket;

import java.io.Serializable;

public class MyObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String field1;
    private String field2;
    public MyObject(String field1,String field2){
        this.field1 = field1;
        this.field2 = field2;
    }

    @Override
    public String toString() {
        return "MyObject{" +
                "field1='" + field1 + '\'' +
                ", field2='" + field2 + '\'' +
                '}';
    }
}
