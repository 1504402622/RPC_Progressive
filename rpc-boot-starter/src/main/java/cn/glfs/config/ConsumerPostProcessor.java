package cn.glfs.config;

import cn.glfs.annotation.RpcReference;
import cn.glfs.common.URL;
import cn.glfs.common.constants.Register;
import cn.glfs.common.constants.RpcProxy;
import cn.glfs.event.RpcListerLoader;
import cn.glfs.filter.FilterFactory;
import cn.glfs.proxy.IProxy;
import cn.glfs.proxy.ProxyFactory;
import cn.glfs.register.RegistryFactory;
import cn.glfs.register.RegistryService;
import cn.glfs.router.LoadBalancerFactory;
import cn.glfs.socket.client.Client;
import cn.glfs.socket.serialization.SerializationFactory;
import cn.glfs.tolerant.FaultTolerantFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;


public class ConsumerPostProcessor implements BeanPostProcessor, InitializingBean {

    RpcProperties rpcProperties;

    public ConsumerPostProcessor(RpcProperties rpcProperties) {
        this.rpcProperties = rpcProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new RpcListerLoader().init();
        FaultTolerantFactory.init();
        RegistryFactory.init();
        FilterFactory.initClient();
        ProxyFactory.init();
        LoadBalancerFactory.init();
        SerializationFactory.init();
        final Client client = new Client();
        client.run();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 获取所有字段
        final Field[] fields = bean.getClass().getDeclaredFields();
        // 遍历所有字段找到 RpcReference 注解的字段
        for (Field field : fields) {
            if(field.isAnnotationPresent(RpcReference.class)){
                final RegistryService registryService = RegistryFactory.get(Register.ZOOKEEPER);
                final Class<?> aClass = field.getType();
                final RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                field.setAccessible(true);
                Object object = null;
                try {
                    final IProxy iproxy = ProxyFactory.get(RpcProxy.CG_LIB);
                    final Object proxy = iproxy.getProxy(aClass,rpcReference);
                    // 创建代理对象
                    object = proxy;
                    final URL url = new URL();
                    url.setServiceName(aClass.getName());
                    url.setVersion(rpcReference.version());
                    registryService.subscribe(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    // 将代理对象设置给字段
                    field.set(bean,object);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
