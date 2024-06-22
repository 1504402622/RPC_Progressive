package cn.glfs.provider.filter;


import cn.glfs.filter.FilterData;
import cn.glfs.filter.FilterResponse;
import cn.glfs.filter.server.ServerBeforeFilter;
import cn.glfs.socket.codec.RpcRequest;

public class TokenFilter implements ServerBeforeFilter {

    @Override
    public FilterResponse doFilter(FilterData<RpcRequest> filterData) {
        final RpcRequest rpcRequest = filterData.getObject();
        Object token = rpcRequest.getClientAttachments().get("token");
        if (!token.equals("glfs")){
            return new FilterResponse(false,new Exception("token不正确"));
        }
        return new FilterResponse(true,null);
    }
}
