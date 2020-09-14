package too.weidong.network.bio;

/**
 * Created with IntelliJ IDEA.
 * Description: 定义消息头
 *  序列化类型   |   消息类型     |     响应码   |    消息id  |    消息体长度 |
 *   1 byte    |    1 byte     |    1 byte   |   8 byte  |     4 byte  |
 *
 *   总长度15字节
 *
 *
 * @author dongwei
 * @date 2018/04/02
 * Time: 17:19
 */
public class SProtocolHeader {

    /** 协议头长度 */
    public static final int HEADER_SIZE = 15;

    /** 序列化类型 jdk protostuff kryo ...*/
    private byte serializerCode;

    /** 消息类型*/
    private byte messageCode;

    public static final byte REQUEST                    = 0x01;     // Request
    public static final byte RESPONSE                   = 0x02;     // Response
    public static final byte PUBLISH_SERVICE            = 0x03;     // 发布服务
    public static final byte PUBLISH_CANCEL_SERVICE     = 0x04;     // 取消发布服务
    public static final byte SUBSCRIBE_SERVICE          = 0x05;     // 订阅服务
    public static final byte OFFLINE_NOTICE             = 0x06;     // 通知下线
    public static final byte ACK                        = 0x07;     // Acknowledge
    public static final byte HEARTBEAT                  = 0x0f;     // Heartbeat

    /** 响应状态码*/
    private byte status;

    /** Invoke id*/
    private long id;

    /** 消息体长度*/
    private int contentSize;

    public byte messageCode() {
        return messageCode;
    }

    public byte serializerCode() {
        return serializerCode;
    }

    public byte status() {
        return status;
    }

    public void status(byte status) {
        this.status = status;
    }

    public long id() {
        return id;
    }

    public void id(long id) {
        this.id = id;
    }

    public int contentSize() {
        return contentSize;
    }

    public void contentSize(int contentSize) {
        this.contentSize = contentSize;
    }

}
