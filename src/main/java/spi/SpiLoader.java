package spi;


import cn.hutool.core.io.resource.ResourceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spi加载器，从配置从读取到实例
 * 因为是自定义，需要在多个实例中作先择，则配置中不再是单纯存在实现类，还要有对应的key
 * 则在接口名的配置=>(key => 实现类）
 */
public class SpiLoader {
    /**
     * 存储已加载的 接口 => （key => 实现类）
     */
    private static final Map<String, Map<String, Class<?>>> loadMap = new ConcurrentHashMap<>();
    /**
     * 存储（实现 => 实现类的实例）
     */
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();
    /**
     * 默认路径
     */
    private static final String PRE_SERIALIZER_DIR = "META-INF/register/";

    /**
     * 从配置从加载到所有类对象
     *
     * @param loadInterface
     */
    public static void load(Class<?> loadInterface) {
        String scanDir = PRE_SERIALIZER_DIR + loadInterface.getName();
        List<URL> resources = ResourceUtil.getResources(scanDir);
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (URL resource : resources) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] split = line.split("=");
                    if (split.length > 1) {
                        String key = split[0];
                        String value = split[1];
                        keyClassMap.put(key, Class.forName(value));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        loadMap.put(loadInterface.getName(), keyClassMap);
    }

    /**
     * 获取实例
     *
     * @param loadInterface
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<?> loadInterface, String key) {
        Map<String, Class<?>> keyClassMap = loadMap.get(loadInterface.getName());
        if (keyClassMap.isEmpty())
            System.out.println(String.format("SpiLoader 未存在 %s 类型", loadInterface.getName()));
        if (!keyClassMap.containsKey(key))
            System.out.println(String.format("SpiLoader %s 中未存在 %s 的实现类", loadInterface, key));
        Class<?> implClass = keyClassMap.get(key);

        if (!instanceCache.containsKey(implClass.getName())) try {
//                instanceCache.put(implClass.getName(),implClass.newInstance()); 在java9后禁用
            instanceCache.put(implClass.getName(), implClass.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return (T) instanceCache.get(implClass.getName());
    }
}

