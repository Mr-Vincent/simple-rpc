package too.weidong.network.bio.payload;

/**
 * Created with IntelliJ IDEA.
 * Description: 响应数据包
 *
 * @author dongwei
 * @date 2018/04/02
 * Time: 17:57
 */
public class SimpleResponsePayload extends PayloadHolder {

    private final long id;
    private byte status;

    public SimpleResponsePayload(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }

    public byte status() {
        return status;
    }

    public void status(byte status) {
        this.status = status;
    }
}
