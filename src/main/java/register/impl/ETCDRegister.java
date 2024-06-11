package register.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;
import register.INFO.Config;
import register.INFO.Meta;
import register.Register;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Slf4j
public class ETCDRegister implements Register {
    private Client client;
    private KV kvClient;
    private final Set<String> nodeKeySet = new HashSet<>();
    private final Set<String> watchKeySet = new ConcurrentHashSet<>();

    /**
     * 建立与第三方注册服务中心的连接
     *
     * @param registryConfig 第三方注册中心的配置
     */
    @Override
    public void init(Config registryConfig) {
        log.info("尝试接入 {} 配置服务中心", registryConfig.getRegistryName());
        client = Client.builder()
                .endpoints(registryConfig.getRegistryAddr())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        keepAlive();
    }

    @Override
    public void register(Meta meta) throws ExecutionException, InterruptedException {
        String keyDir = meta.getKeyDir();
        ByteSequence key = ByteSequence.from(keyDir, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(meta), StandardCharsets.UTF_8);
        Lease lease = client.getLeaseClient();
        long leaseId = lease.grant(30).get().getID();
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();

        kvClient.put(key, value, putOption).get();
//        成功注册后就放入key本地缓存
        nodeKeySet.add(keyDir);
    }

    @Override
    public void disRegister(Meta meta) {
        String keyDir = meta.getKeyDir();
        ByteSequence key = ByteSequence.from(keyDir, StandardCharsets.UTF_8);
        kvClient.delete(key);
//        注销服务后也要从本地缓存中删除
        nodeKeySet.remove(keyDir);
    }

    @Override
    public List<Meta> getService(Meta serverMetaInfo) throws ExecutionException, InterruptedException {
        String preSearchKey = "/" + serverMetaInfo.getPreFixDir() + "/" + serverMetaInfo.getServiceName() + "/";
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        ByteSequence preKey = ByteSequence.from(preSearchKey, StandardCharsets.UTF_8);
        List<KeyValue> keyValueList = kvClient.get(preKey, getOption).get().getKvs();
        return keyValueList.stream().map(keyValue -> {
            String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
            watch(key);
            String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
            return JSONUtil.toBean(value, Meta.class);
        }).collect(Collectors.toList());
    }

    /**
     * 续约
     */
    public void keepAlive() {
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            for (String key : nodeKeySet) {
                try {
                    List<KeyValue> values = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
//                    如果已经过期的服务，就只有服务端自己重新注册了
                    if (CollUtil.isEmpty(values))
                        continue;
                    KeyValue value = values.get(0);
                    String s = value.getValue().toString(StandardCharsets.UTF_8);
                    Meta serverMetaInfo = JSONUtil.toBean(s, Meta.class);
//                    重新注册快到期的服务就是续期了
                    register(serverMetaInfo);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();

    }

    /**
     * 监听
     */
    public void watch(String key) {
        Watch watchClient = client.getWatchClient();
//        set集合一个对象只能存在一个
        boolean newWatch = watchKeySet.add(key);
        if (newWatch) {
            watchClient.watch(ByteSequence.from(key, StandardCharsets.UTF_8), watchResponse -> {
                for (WatchEvent event : watchResponse.getEvents())
                    switch (event.getEventType()) {
                        case DELETE:
                        case PUT:
                        case UNRECOGNIZED:
                        default:
                            break;
                    }
            });
        }
    }
}
