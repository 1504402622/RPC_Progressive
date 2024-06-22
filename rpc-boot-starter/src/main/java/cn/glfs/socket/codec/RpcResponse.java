package cn.glfs.socket.codec;

import java.io.Serializable;


public class RpcResponse implements Serializable {
    private Object data;
    private Exception exception;

    public RpcResponse() {
    }

    public Exception getException() {
        return exception;
    }

    public Object getData() {
        return data;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
