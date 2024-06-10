package register;

import register.INFO.Meta;

public interface Register {
    /**
     * 注册服务
     *
     * @param service  服务名
     * @param implHost 实现ip
     */
    void register(Meta meta);

    /**
     * 删除服务
     *
     * @param service 服务名
     */
    void disRegister(Meta meta);

    /**
     * 获取服务
     *
     * @param service 服务名
     * @return 服务名指向的实现ip
     */
    String getService(Meta meta);
}
