import register.INFO.Config;
import register.INFO.Meta;
import register.Register;

import java.util.concurrent.ExecutionException;

public class test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Register register = RegisterFactory.getInstance("etcd");
//        配置连接
        Meta meta = Meta.builder()
                .serviceName("asouwn")
                .serviceUrl("http://hello/asouwn")
                .serviceVision("1.0.0").build();

        Config config = Config.builder()
                .registryName("asouwn")
                .registryAddr("http://127.0.0.1:2379")
                .timeout(3000L).build();
        register.init(config);
//        消费
        System.out.println(register.getService(meta));
    }
}
