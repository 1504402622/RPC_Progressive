package cn.glfs.consumer.filter;


import cn.glfs.filter.FilterData;
import cn.glfs.filter.FilterResponse;
import cn.glfs.filter.client.ClientBeforeFilter;
import cn.glfs.socket.codec.RpcRequest;

public class TokenFilter implements ClientBeforeFilter {
    @Override
    public FilterResponse doFilter(FilterData<RpcRequest> filterData) {
        final RpcRequest rpcRequest = filterData.getObject();
        rpcRequest.getClientAttachments().put("token","glfs");
        return new FilterResponse(true,null);
    }
}
