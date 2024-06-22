package cn.glfs.filter.client;

import cn.glfs.filter.Filter;
import cn.glfs.socket.codec.RpcResponse;

public interface ClientAfterFilter extends Filter<RpcResponse> {
}
