package top.weidong.registry.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.network.Directory;
import top.weidong.registry.RegisterMeta;
import top.weidong.registry.Registry;
import top.weidong.registry.RegistryService;

import static top.weidong.common.util.Preconditions.checkNotNull;
import static top.weidong.common.util.StackTraceUtil.stackTrace;

/**
 * Created with IntelliJ IDEA.
 * Description: 封装一个zk客户端
 *
 * @author dongwei
 * @date 2018/03/29
 * Time: 17:37
 */
public class ZookeeperRegistryService implements Registry,RegistryService {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ZookeeperRegistryService.class);
    /**目录*/
    private static final String PROVIDER_PATH = "/simple/provider";

    private CuratorFramework client;

    private String serviceAddress;


    @Override
    public void connectToRegistryServer(String connectString) {
        checkNotNull(connectString, "connectString");
        int sessionTimeoutMs = 5000;
        int connectionTimeoutMs = 5000;
        client = CuratorFrameworkFactory.newClient(
                connectString, sessionTimeoutMs, connectionTimeoutMs, new ExponentialBackoffRetry(500, 20));

        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                LOGGER.debug("connection state changed {}.....",newState);
                if (newState == ConnectionState.RECONNECTED) {
                    LOGGER.debug("connection reconnect.....");
                }
            }
        });
        client.start();
    }

    /**
     * 注册一个服务
     * @param meta 注册信息 包含服务名称和服务地址
     */
    @Override
    public void register(final Directory meta){
        LOGGER.info("RegisterMeta:{}",meta);
        String directory = PROVIDER_PATH + "/" + meta.getServiceProviderName();
        try {
            if (client.checkExists().forPath(directory) == null) {
                client.create()
                        .creatingParentContainersIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .inBackground(new BackgroundCallback() {
                            @Override
                            public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                                if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                                    LOGGER.info("Register: {} - {}.", meta, event);
                                }
                            }
                        })
                        .forPath(directory,meta.getServerAddress().getBytes());
            } else {
                client.setData().forPath(directory,meta.getServerAddress().getBytes());
            }
        } catch (Exception e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Create parent path failed, directory: {}, {}.", directory, stackTrace(e));
            }
        }

    }

    /**
     * 订阅一个服务
     * @param meta
     */
    @Override
    public void subscribe(Directory meta){
        String directory = PROVIDER_PATH + "/" + meta.getServiceProviderName();
        PathChildrenCache childrenCache = new PathChildrenCache(client, directory, false);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                LOGGER.info("Child event: {}", pathChildrenCacheEvent);
                switch (pathChildrenCacheEvent.getType()) {
                    // todo 回调实现
                    case CHILD_ADDED: {
                        LOGGER.info("child added");
                        break;
                    }
                    case CHILD_REMOVED: {
                        LOGGER.info("child removed");
                        break;
                    }
                    default:{
                        LOGGER.info("unknown event");
                    }
                }
            }
        });
        try {
            childrenCache.start();
            byte[] data = client.getData().forPath(directory);
            serviceAddress = new String(data);
            LOGGER.info("节点{}的数据：{}",directory,serviceAddress);
        } catch (Exception e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Subscribe {} failed, {}.", directory, stackTrace(e));
            }
        }

        LOGGER.info("subscribe success");
    }

    /**
     * 获取服务地址
     * @return
     */
    @Override
    public String getServiceAddress() {
        return serviceAddress;
    }
}
