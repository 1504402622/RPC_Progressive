package cn.glfs.common;

import io.netty.util.concurrent.Promise;

/**
 * 提供RpcFucture使用Promise发起异步任务(携带超时完成时间)
 * @param <T>
 */
public class RpcFuture<T> {
    private Promise<T> promise;
    private long timeout;
    public Promise<T> getPromise() {
        return promise;
    }

    public void setPromise(Promise<T> promise) {
        this.promise = promise;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public RpcFuture() {
    }

    public RpcFuture(Promise<T> promise, long timeout) {
        this.promise = promise;
        this.timeout = timeout;
    }
}
