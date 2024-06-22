package cn.glfs.filter.client;

import cn.glfs.filter.Filter;
import cn.glfs.socket.codec.RpcRequest;


public interface ClientBeforeFilter extends Filter<RpcRequest> {
}
