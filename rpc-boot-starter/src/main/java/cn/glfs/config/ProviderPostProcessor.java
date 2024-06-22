package cn.glfs.config;

import cn.glfs.annotation.RpcService;
import cn.glfs.common.Cache;
import cn.glfs.common.URL;
import cn.glfs.filter.FilterFactory;
import cn.glfs.invoke.InvokerFactory;
import cn.glfs.register.RegistryFactory;
import cn.glfs.register.RegistryService;
import cn.glfs.socket.serialization.SerializationFactory;
import cn.glfs.socket.server.Server;
import cn.glfs.utils.IpUtil;
import cn.glfs.utils.ServiceNameBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;


public class ProviderPostProcessor implements InitializingBean, BeanPostProcessor {

    private RpcProperties rpcProperties;

    private final String ip = IpUtil.getIP();

    public ProviderPostProcessor(RpcProperties rpcProperties) {
        this.rpcProperties = rpcProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RegistryFactory.init();
        FilterFactory.initServer();
        InvokerFactory.init();
        SerializationFactory.init();
        Thread t = new Thread(() -> {
            final Server server = new Server(rpcProperties.getPort());
            try {
                server.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        // 将线程 t 设置为守护线程
        t.setDaemon(true);
        t.start();
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 找到bean上带有 RpcService 注解的类
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 可能会有多个接口,默认选择第一个接口
            String serviceName = beanClass.getInterfaces()[0].getName();
            if (!rpcService.serviceInterface().equals(void.class)){
                serviceName = rpcService.serviceInterface().getName();
            }
            try {
                RegistryService registryService = RegistryFactory.get(rpcProperties.getRegistry().getName());
                final URL url = new URL();
                url.setPort(Properties.getPort());
                url.setIp(ip);
                url.setServiceName(serviceName);
                url.setVersion(rpcService.version());
                registryService.register(url);
                // 缓存
                final String key = ServiceNameBuilder.buildServiceKey(serviceName, rpcService.version());
                Cache.SERVICE_MAP.put(key,bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bean;
    }
}
