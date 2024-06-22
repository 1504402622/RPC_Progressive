package cn.glfs.socket.client;

import cn.glfs.filter.FilterData;
import cn.glfs.filter.FilterResponse;
import cn.glfs.filter.client.ClientBeforeFilter;
import cn.glfs.socket.codec.RpcRequest;

public class ClientTokenFilter implements ClientBeforeFilter {

    @Override
    public FilterResponse doFilter(FilterData<RpcRequest> filterData) {
        System.out.println("客户端前置拦截器添加token为glfs");
        final RpcRequest rpcRequest = filterData.getObject();
        rpcRequest.getClientAttachments().put("token","glfs");
        return new FilterResponse(true,null);
    }
}
