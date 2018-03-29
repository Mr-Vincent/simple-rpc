package top.weidong.registry;

/**
 * Created with IntelliJ IDEA.
 * Description: 注册器 主要用于连接到注册中心
 *
 * @author dongwei
 * @date 2018/03/29
 * Time: 17:27
 */
public interface Registry {

    /**
     * Establish connections with registry server.
     *
     * 连接注册中心, 可连接多个地址.
     *
     * @param connectString list of servers to connect to [host1:port1,host2:port2....]
     */
    void connectToRegistryServer(String connectString);
}
