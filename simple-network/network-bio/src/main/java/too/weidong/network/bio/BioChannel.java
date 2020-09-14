package too.weidong.network.bio;

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
 * BIO实现
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
            getOutputStream().write(requestPayload.bytes());
            // 消息体 body的长度，int类型  也需要处理一下
            int bodySize = requestPayload.bytes().length;
            IoUtil.writeInt(getOutputStream(),bodySize);
            // 消息体
            getOutputStream().write(requestPayload.bytes());

        }else if(message instanceof SimpleResponsePayload){

        }
        return null;
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
}
