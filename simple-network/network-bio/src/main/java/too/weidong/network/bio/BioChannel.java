package too.weidong.network.bio;

import too.weidong.network.bio.payload.HeartBeats;
import too.weidong.network.bio.payload.SimpleRequestPayload;
import too.weidong.network.bio.payload.SimpleResponsePayload;
import top.weidong.common.util.IoUtil;
import top.weidong.common.util.Maps;
import top.weidong.network.api.Channel;
import top.weidong.network.api.Payload;
import top.weidong.network.api.PayloadHeader;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

/**
 * @author dongwei
 * @since 2020/09/14
 * Time: 15:26
 * BIO channel 实现
 */
public class BioChannel implements Channel {

    private Socket socket;

    private static Map<Socket,Channel> map = Maps.newConcurrentMap();

    private String id;

    public BioChannel(){
        this.id = UUID.randomUUID().toString();
    }
    public BioChannel(Socket socket){
        this.socket = socket;
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Channel write(Payload message) throws IOException {
        // 请求消息
        if(message instanceof SimpleRequestPayload){
            SimpleRequestPayload requestPayload = (SimpleRequestPayload)message;
            // 标识位 分为低位和高位
            byte low = PayloadHeader.REQUEST;
            byte high = requestPayload.serializerCode();
            byte sign = PayloadHeader.toSign(high, low);
            getOutputStream().write(sign);
            // 请求响应状态
            getOutputStream().write(0x00);
            // invoke id long类型，需要处理一下
            IoUtil.writeLong(getOutputStream(),requestPayload.invokeId());
            // 消息体 body的长度，int类型  也需要处理一下
            int bodySize = requestPayload.bytes().length;
            IoUtil.writeInt(getOutputStream(),bodySize);
            // 消息体
            getOutputStream().write(requestPayload.bytes());
        }else if(message instanceof SimpleResponsePayload){
            // 响应消息
            SimpleResponsePayload responsePayload = (SimpleResponsePayload)message;
            // 标识位 分为低位和高位
            byte low = PayloadHeader.REQUEST;
            byte high = responsePayload.serializerCode();
            byte sign = PayloadHeader.toSign(high, low);
            getOutputStream().write(sign);
            byte status = responsePayload.status();
            getOutputStream().write(status);
            IoUtil.writeLong(getOutputStream(),responsePayload.id());
            byte[] bytes = responsePayload.bytes();
            int length = bytes.length;
            IoUtil.writeInt(getOutputStream(),length);
            getOutputStream().write(bytes);

        }else if(message instanceof HeartBeats){
            // 心跳包 只有头信息，其他的都给空就好了 这里只做一个标志
            HeartBeats heartBeats = (HeartBeats)message;
            byte low = PayloadHeader.HEARTBEAT;
            // 不用区分高位地位了
            getOutputStream().write(low);
            getOutputStream().write(0);
            IoUtil.writeLong(getOutputStream(),0);
            IoUtil.writeInt(getOutputStream(),0);
        }
        return this;
    }

    private OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }


    public static Channel ofChannel(Socket socket){
        Channel channel = map.get(socket);
        if(null == channel){
            BioChannel bioChannel = new BioChannel(socket);
            Channel put = map.putIfAbsent(socket, bioChannel);
            if(put == null){
                channel = bioChannel;
            }
        }
        return channel;
    }

    @Override
    public String toString() {
        return "id->" + id + " remote address->"+socket.getInetAddress()+ "  port:" + socket.getPort();
    }
}
