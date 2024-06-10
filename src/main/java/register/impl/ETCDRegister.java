//package register.impl;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.collection.ConcurrentHashSet;
//import cn.hutool.cron.CronUtil;
//import cn.hutool.cron.task.Task;
//import cn.hutool.json.JSONUtil;
//import io.etcd.jetcd.*;
//import io.etcd.jetcd.options.PutOption;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import register.INFO.Config;
//import register.INFO.Meta;
//import register.Register;
//
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ExecutionException;
//
//@Slf4j
//public class ETCDRegister implements Register<Class<?>> {
//    private Client client;
//    private KV kvClient;
//    private final String PreFixDir = "/asrpc/";
//    private final Set<String> nodeKeySet = new HashSet<>();
//    private final Set<String> watchKeySet = new ConcurrentHashSet<>();
//    public ETCDRegister(Config config){
//        init(config);
//    }
//    /**
//     * 建立与第三方注册服务中心的连接
//     * @param registryConfig 第三方注册中心的配置
//     */
//    private void init(Config registryConfig){
//        log.info("尝试接入 {} 配置服务中心", registryConfig.getRegistryName());
//        client = Client.builder()
//                .endpoints(registryConfig.getRegistryAddr())
//                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
//                .build();
//        kvClient = client.getKVClient();
//        keepAlive();
//    }
//    @Override
//    public void register(Meta meta) {
//        String keyDir = PreFixDir+serverMetaInfo.getKey();
//        ByteSequence key = ByteSequence.from(keyDir, StandardCharsets.UTF_8);
//        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serverMetaInfo), StandardCharsets.UTF_8);
//        Lease lease = client.getLeaseClient();
//        long leaseId = lease.grant(30).get().getID();
//        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
//
//        kvClient.put(key, value, putOption).get();
////        成功注册后就放入key本地缓存
//        nodeKeySet.add(keyDir);
//    }
//
//    @Override
//    public void disRegister(Meta meta) {
//
//    }
//
//    @Override
//    public Class<?> getService(Meta meta) {
//        return null;
//    }
//    /**
//     * 续约
//     */
//    public void keepAlive(){
//        CronUtil.schedule("*/10 * * * * *", (Task) ()->{
//            for (String key : nodeKeySet){
//                try {
//                    List<KeyValue> values = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
////                    如果已经过期的服务，就只有服务端自己重新注册了
//                    if (CollUtil.isEmpty(values))
//                        continue;
//                    KeyValue value = values.get(0);
//                    String s = value.getValue().toString(StandardCharsets.UTF_8);
//                    Meta serverMetaInfo = JSONUtil.toBean(s, ServerMetaInfo.class);
////                    重新注册快到期的服务就是续期了
//                    register(serverMetaInfo);
//                } catch (InterruptedException | ExecutionException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//
//        CronUtil.setMatchSecond(true);
//        CronUtil.start();
//
//    }
//}
