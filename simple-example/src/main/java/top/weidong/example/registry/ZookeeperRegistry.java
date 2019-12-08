package top.weidong.example.registry;

import top.weidong.registry.RegisterMeta;
import top.weidong.registry.zk.ZookeeperRegistryService;

import java.io.IOException;

/**
 * @author dongwei
 * @since 2019/12/02
 * Time: 16:11
 */
public class ZookeeperRegistry {

    private static ZookeeperRegistryService zookeeperRegistryService = new ZookeeperRegistryService();

    private static final String ZK_ADDRESS = "172.16.0.64";
    public static void main(String[] args) throws IOException {
        zookeeperRegistryService.connectToRegistryServer(ZK_ADDRESS);
        RegisterMeta registerMeta = new RegisterMeta();
        registerMeta.setProviderName("test");
        registerMeta.setServerAddress("127.0.0.1:20881");
        register(registerMeta);
        System.in.read();

        subscribe(registerMeta);
        System.in.read();
    }

    private static void register(RegisterMeta meta){
        meta.setProviderName("test");
        meta.setServerAddress("127.0.0.1:20881");
        zookeeperRegistryService.register(meta);
    }

    private static void subscribe(RegisterMeta meta){
        meta.setProviderName("test");
        meta.setServerAddress("127.0.0.1:20881");
        zookeeperRegistryService.subscribe(meta);
    }
}
