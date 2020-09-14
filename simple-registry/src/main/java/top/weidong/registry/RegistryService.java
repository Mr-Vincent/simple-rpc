package top.weidong.registry;

import network.Directory;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/30
 * Time: 09:41
 */
public interface RegistryService extends Registry{

    /**
     * 注册一个服务
     * @param directory
     */
    void register(Directory directory);

    /**
     * 订阅一个服务
     * @param directory
     */
    void subscribe(Directory directory);

    /**
     *
     * @return
     */
    String getServiceAddress();
}
