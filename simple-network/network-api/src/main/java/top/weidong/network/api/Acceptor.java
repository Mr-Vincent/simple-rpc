package top.weidong.network.api;

import java.io.IOException;
import java.util.List;

/**
 * @author dongwei
 * @since 2020/09/14
 * Time: 11:45
 * 用于接收客户端请求
 */
public interface Acceptor {

    /**
     * start acceptor
     */
    void start() throws IOException;

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 设置处理器
     * @param processor
     */
    void processor(ProviderProcessor processor);

    /**
     * 与服务端关联的所有连接
     * @return
     */
    List<Channel> allChannels();
}
