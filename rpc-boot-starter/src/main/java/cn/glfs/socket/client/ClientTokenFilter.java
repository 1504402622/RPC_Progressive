package cn.glfs.socket.client;

import cn.glfs.filter.FilterData;
import cn.glfs.filter.FilterResponse;
import cn.glfs.filter.client.ClientBeforeFilter;
import cn.glfs.socket.codec.RpcRequest;


public class ClientTokenFilter implements ClientBeforeFilter {
    @Override
    public FilterResponse doFilter(FilterData<RpcRequest> filterData) {
        final RpcRequest rpcRequest = filterData.getObject();
        rpcRequest.getClientAttachments().put("token","xhy");
        return new FilterResponse(true,null);
    }
}
