package register;

public interface Register {
    /**
     * 注册服务
     *
     * @param service  服务名
     * @param implHost 实现ip
     */
    void register(String service, String implHost);

    /**
     * 删除服务
     *
     * @param service 服务名
     */
    void disRegister(String service);

    /**
     * 获取服务
     *
     * @param service 服务名
     * @return 服务名指向的实现ip
     */
    String getService(String service);
}
