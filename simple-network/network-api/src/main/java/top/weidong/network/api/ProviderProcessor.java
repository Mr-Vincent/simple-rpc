package top.weidong.network.api;

/**
 * @author dongwei
 * @since 2020/09/14
 * Time: 12:33
 */
public interface ProviderProcessor {

    /**
     * 处理正常请求
     */
    void handleRequest() throws Exception;

    /**
     * 处理异常
     */
    void handleException(Throwable cause);

    /**
     * 关闭
     */
    void shutdown();
}
