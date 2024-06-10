package register.INFO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Config {
    /**
     * 注册器的选择
     */
    private String registryName;
    /**
     * 注册中心的服务地址 http://...格式
     */
    private String registryAddr;
    /**
     * 注册期限，用于心跳检测，超过时限没有更新，则说明服务故障，下线
     */
    private long timeout;
}
