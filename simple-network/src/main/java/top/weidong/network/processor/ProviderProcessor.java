package top.weidong.network.processor;

import top.weidong.network.Status;
import top.weidong.network.payload.SRequestPayload;

/**
 * Created with IntelliJ IDEA.
 * Description: 服务提供者处理器 仅处理业务逻辑
 *
 * @author dongwei
 * @date 2018/04/02
 * Time: 17:59
 */
public interface ProviderProcessor {
    /**
     * 处理正常请求
     */
    void handleRequest(SRequestPayload request) throws Exception;

    /**
     * 处理异常
     */
    void handleException(SRequestPayload request, Status status, Throwable cause);

    void shutdown();
}
