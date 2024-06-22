package cn.glfs.router;


import cn.glfs.common.URL;

import java.util.List;

public interface LoadBalancer {

    URL select(List<URL> urls);
}
