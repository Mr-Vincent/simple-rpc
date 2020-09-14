package too.weidong.network.bio.payload;

import top.weidong.network.api.Payload;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/02
 * Time: 17:37
 */
public abstract class PayloadHolder implements Payload {

    private byte serializerCode;

    private byte[] bytes;

    public byte serializerCode() {
        return serializerCode;
    }

    public byte[] bytes() {
        return bytes;
    }

    public void bytes(byte serializerCode, byte[] bytes) {
        this.serializerCode = serializerCode;
        this.bytes = bytes;
    }


}
