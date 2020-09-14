package too.weidong.network.bio.processor;

import too.weidong.network.bio.Status;
import too.weidong.network.bio.payload.SRequestPayload;

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
