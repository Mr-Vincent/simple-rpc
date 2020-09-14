package top.weidong.service;

import top.weidong.common.util.NetUtil;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import network.SServer;
import network.enums.ProcessorType;
import too.weidong.network.bio.processor.Processor;
import top.weidong.registry.RegisterMeta;
import top.weidong.registry.RegistryService;
import top.weidong.service.processor.ProcessorFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: 默认服务器
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 10:16
 */
public class DefaultServer {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(DefaultServer.class);

    private SServer server;

    private ProcessorType type;

    private RegistryService registryService;

    private Map<String, Object> handlerMap = new HashMap<>();

    private String serverAddress;

    public DefaultServer() {
    }

    public DefaultServer(RegistryService registryService) {
        this.registryService = registryService;
    }

    public DefaultServer(RegistryService registryService, int exposePort) {
        this.registryService = registryService;
        this.serverAddress = resolveToAddress(exposePort);
    }

    public DefaultServer(ProcessorType type, RegistryService registryService) {
        this.registryService = registryService;
        this.type = type;
    }

    /**
     * 构造本机暴露的服务地址--> ip ： port
     * @param port
     * @return ip ： port
     */
    private String resolveToAddress(int port){
       return NetUtil.getLocalAddress() +":"+ Integer.toString(port);
    }

    /**
     * 添加server
     *
     * @param server
     * @return
     */
    public DefaultServer withServer(SServer server) {
        Processor processor = null;
        if (null == type) {
            processor = ProcessorFactory.newInstance(ProcessorType.RPC,handlerMap);
        } else {
            processor = ProcessorFactory.newInstance(type,handlerMap);
        }
        LOGGER.debug(">>>>>>>>>>>add processor>>>>>>>>>>>：[{}]", processor.getClass().getName());
        if (server.getProcessor() == null) {
            server.withProcessor(processor);
        }
        this.server = server;
        return this;
    }

    /**
     * 发布服务 本地发布和发布到注册中心
     * 实质上就是将服务端的接口名存到配置中心
     * 消费端订阅这个节点 根据节点的值（接口名）去向服务端发送请求
     *
     * @param clazz
     * @param object
     */
    public void publish(Class clazz,Object object) {
        LOGGER.debug(">>>>>>>>>>>publish service to registry>>>>>>>>>>>：[{}]", clazz.getName());
        registryService.register(RegisterMeta.fromClazz(clazz,serverAddress));
        addService(clazz.getName(),object);
    }

    /**
     * 添加服务 将需要暴露的服务对象存储在本地
     * @param interfaceName 接口名
     * @param object 实现对象
     */
    public void addService(String interfaceName, Object object) {
        if (!handlerMap.containsKey(interfaceName)) {
            LOGGER.info("Loading service: {}", interfaceName);
            handlerMap.put(interfaceName, object);
        }
    }

    /**
     * 启动服务
     *
     * @throws IOException
     */
    public void start() throws IOException {
        LOGGER.debug(">>>>>>>>>>provider start>>>>>>>>>>>");
        server.start();
    }
}
