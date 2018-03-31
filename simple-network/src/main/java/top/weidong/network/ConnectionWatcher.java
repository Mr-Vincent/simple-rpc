package top.weidong.network;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/31
 * Time: 11:00
 */
public interface ConnectionWatcher {
    /**
     * 等待连接可用
     * @param timeoutMillis 超时时间
     * @return
     */
    boolean waitForAvailable(long timeoutMillis);
}
