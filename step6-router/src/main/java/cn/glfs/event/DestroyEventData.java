package cn.glfs.event;


public class DestroyEventData implements RpcEventData{

    private Object o;

    public DestroyEventData(Object o) {
        this.o = o;
    }

    @Override
    public void setData(Object o) {
        this.o = o;
    }

    @Override
    public Object getData() {
        return o;
    }
}
