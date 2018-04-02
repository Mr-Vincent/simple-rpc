package top.weidong.network.payload;

/**
 * Created with IntelliJ IDEA.
 * Description: 请求数据包
 *
 * @author dongwei
 * @date 2018/04/02
 * Time: 17:38
 */
public class SRequestPayload extends PayloadHolder {

    private final long invokeId;

    private transient long timestamp;

    public SRequestPayload(long invokeId) {
        this.invokeId = invokeId;
    }

    public long invokeId() {
        return invokeId;
    }

    public long timestamp() {
        return timestamp;
    }

    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
