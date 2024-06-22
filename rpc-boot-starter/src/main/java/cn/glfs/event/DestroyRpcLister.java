package cn.glfs.event;

import cn.glfs.common.Cache;
import cn.glfs.common.Host;
import cn.glfs.common.ServiceName;
import cn.glfs.common.URL;


public class DestroyRpcLister implements IRpcLister<DestroyEventData> {
    @Override
    public void exec(DestroyEventData destroyEventData) {
        final URL url = (URL) destroyEventData.getData();
        final ServiceName serviceName = new ServiceName(url.getServiceName(), url.getVersion());
        if (Cache.SERVICE_URLS.containsKey(serviceName)){

            Cache.SERVICE_URLS.get(serviceName).remove(url);
        }
        final Host ip = new Host(url.getIp(), url.getPort());
        if (Cache.CHANNEL_FUTURE_MAP.containsKey(ip)) {
            Cache.CHANNEL_FUTURE_MAP.remove(ip);
        }
    }
}
