package cn.glfs.event;


public interface IRpcLister<T> {

    void exec(T t);
}

