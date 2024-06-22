package cn.glfs.socket.server;


import cn.glfs.filter.FilterData;
import cn.glfs.filter.FilterResponse;
import cn.glfs.filter.server.ServerBeforeFilter;
import cn.glfs.socket.codec.RpcRequest;

public class ServerTokenFilter implements ServerBeforeFilter {

    @Override
    public FilterResponse doFilter(FilterData<RpcRequest> filterData) {
        System.out.println("服务端前置拦截器验证token为glfs");
        final RpcRequest rpcRequest = filterData.getObject();
        Object value = rpcRequest.getClientAttachments().get("token");
        if (!value.equals("glfs")){
            return new FilterResponse(false,new Exception("token 不正确,当前token为:" + value));
        }
        return new FilterResponse(true,null);
    }
}
