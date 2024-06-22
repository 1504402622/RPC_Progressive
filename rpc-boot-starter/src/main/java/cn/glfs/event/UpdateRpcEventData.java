package cn.glfs.event;


public class UpdateRpcEventData implements RpcEventData {

    private Object data;

    public UpdateRpcEventData(Object data) {
        this.data = data;
    }

    @Override
    public void setData(Object o) {
        this.data = o;
    }

    @Override
    public Object getData() {
        return data;
    }
}
