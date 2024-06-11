package register;

import register.INFO.Config;
import register.INFO.Meta;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface Register {
    void init(Config config);
    /**
     * 注册服务
     *
     * @param meta  服务元
     */
    void register(Meta meta) throws ExecutionException, InterruptedException;

    /**
     * 删除服务
     *
     * @param meta  服务元
     */
    void disRegister(Meta meta);

    /**
     * 获取服务
     *
     * @param meta  服务元
     * @return 服务名指向的实现Url
     */
    List<Meta> getService(Meta meta) throws ExecutionException, InterruptedException;
}
