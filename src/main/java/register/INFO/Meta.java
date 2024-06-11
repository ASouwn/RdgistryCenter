package register.INFO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/**
 * 服务元
 * 要注册的服务元信息，简化注册器的使用
 */
public class Meta {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务提供的地址 http://...格式
     */
    private String serviceUrl;
    /**
     * 版本号，方便版本控制
     */
    private String serviceVision;

    private String preFixDir;
    /**
     * 注册服务中心的key
     * @return serviceName:serviceVision/serviceHost:servicePort
     */
    public String getKey(){
        return String.format("%s/%s:%s", serviceName, serviceVision, serviceUrl);
    }
    public String getKeyDir(){
        return String.format("/%s/%s", preFixDir, getKey());
    }

}
