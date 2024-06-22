package cn.glfs.spi;


import io.netty.handler.codec.string.LineSeparator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionLoader {

    // 系统SPI目录前缀
    private static String SYS_EXTENSION_LOADER_DIR_PREFIX = "META-INF/xrpc/";
    // 用户SPI目录前缀
    private static String DIY_EXTENSION_LOADER_DIR_PREFIX = "META-INF/rpc/";
    // 所有的SPI目录前缀
    private static String[] prefixes = {SYS_EXTENSION_LOADER_DIR_PREFIX, DIY_EXTENSION_LOADER_DIR_PREFIX};
    // 缓存单例对象
    private static Map<String,Object> singletonsObject = new ConcurrentHashMap<>();
    // 缓存类定义信息，key为类名，value为类的Class对象
    private static Map<String, Class<?>> extensionClassCache = new ConcurrentHashMap<>();
    // 缓存接口下的所有实现类信息，key为接口名，value为该接口下的实现类的Class对象集合
    private static Map<String, Map<String, Class<?>>> extensionClassCaches = new ConcurrentHashMap<>();

    // 单例对象
    private static ExtensionLoader extensionLoader;
    static {
        extensionLoader = new ExtensionLoader();
    }
    // 获取ExtensionLoader实例的静态方法
    public static ExtensionLoader getInstance() {
        return extensionLoader;
    }

    // 私有构造函数，确保只能通过getInstance方法获取实例
    private ExtensionLoader() {
    }

    /**
     * 根据名称获取单例对象
     */
    public <V> V get(String name){
        if(!singletonsObject.containsKey(name)){
            try {
                // 如果单例对象不存在，则通过反射实例化并放入缓存,方法用于创建并返回该类的一个新实例
                singletonsObject.put(name, extensionClassCache.get(name).newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 返回单例对象
        return (V) singletonsObject.get(name);
    }

    /**
     * 获取指定接口下所有实现类的实例集合
     */
    public List gets(Class<?> clazz){
        final String name = clazz.getName();
        if(!extensionClassCaches.containsKey(name)){
            try {
                // 如果接口名未找到对应实现类集合，则抛出ClassNotFoundException异常
                throw new ClassNotFoundException(clazz + "未找到");
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        final Map<String, Class<?>> classMap = extensionClassCaches.get(name);
        List<Object> objects = new ArrayList<>();
        if(classMap.size() > 0){
            classMap.forEach((k,v)->{
                try {
                    // 将实现类的单例对象加入集合中,从 singletonsObject 中获取键为 k 的值，如果不存在则使用 v 的类对象创建一个新实例作为默认值
                    objects.add(singletonsObject.getOrDefault(k,v.newInstance()));
                }catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
        // 返回实现类的集合
        return objects;
    }

    /**
     * 根据SPI机制加载bean的信息并放入缓存
     */
    public void loadExtension(Class<?> clazz)throws IOException, ClassNotFoundException {
        if(clazz==null) throw new IllegalArgumentException("class 没找到");
        ClassLoader classLoader = this.getClass().getClassLoader();
        Map<String, Class<?>> classMap = new HashMap<>();

        // 遍历系统和用户的SPI目录
        for(String prefix:prefixes){
            String spiFilePath = prefix+clazz.getName();
            // 从给定的类加载器 classLoader 中获取资源路径为 spiFilePath 的所有资源的枚举（Enumeration）
            Enumeration<URL> enumeration = classLoader.getResources(spiFilePath);
            // 枚举对象是 Java 中一种迭代器，可以用来遍历一系列元素
            while (enumeration.hasMoreElements()){
                URL url = enumeration.nextElement();
                try(InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)){
                    String line;
                    // 读取每行的定义,格式为key=value,key为bean的名称,value为具体实现类的全限定名
                    while ((line = bufferedReader.readLine()) != null){
                        String[] lineArr = line.split("=");
                        String key = lineArr[0].trim();
                        String name = lineArr[1].trim();
                        //加载类并放入缓存
                        final Class<?> aClass = Class.forName(name);
                        extensionClassCache.put(key, aClass);
                        classMap.put(key, aClass);
                    }
                }
            }
        }
        // 将接口对应的实现类信息放入缓存
        extensionClassCaches.put(clazz.getName(), classMap);
    }
}
