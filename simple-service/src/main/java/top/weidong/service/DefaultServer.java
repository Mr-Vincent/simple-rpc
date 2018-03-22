package top.weidong.service;

import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.network.SServer;
import top.weidong.network.enums.ProcessorType;
import top.weidong.network.processor.Processor;
import top.weidong.service.processor.ProcessorFactory;

import java.io.IOException;

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

    public DefaultServer(){
    }

    /**
     * 添加server
     * @param server
     * @return
     */
    public DefaultServer withServer(SServer server){
        Processor processor = ProcessorFactory.newInstance(ProcessorType.RPC);
        LOGGER.debug("添加处理器：[{}]",processor.getClass().getName());
        if (server.getProcessor() == null) {
            server.withProcessor(processor);
        }
        this.server = server;
        return this;
    }

    /**
     * 发布服务(本地)
     * @param name
     * @param clazz
     */
    public void publish(String name,Class clazz){
        LOGGER.debug("发布服务：[{}]",clazz.getName());
        SimpleContext.getServiceRegistry().put(name,clazz);
    }

    /**
     * 启动服务
     * @throws IOException
     */
    public void start() throws IOException {
        LOGGER.debug("启动服务器");
        server.start();
    }
}
