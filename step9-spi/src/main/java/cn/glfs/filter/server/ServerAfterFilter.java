package cn.glfs.filter.server;

import cn.glfs.filter.Filter;
import cn.glfs.socket.codec.RpcResponse;

public interface ServerAfterFilter extends Filter<RpcResponse> {
}
