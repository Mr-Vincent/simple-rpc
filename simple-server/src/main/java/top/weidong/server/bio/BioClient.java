package top.weidong.server.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description: BIO 客户端
 *
 * @author dongwei
 * @date 2018/03/20
 * Time: 09:37
 */
public class BioClient {

    private Socket socket = null;

    public BioClient(){
        socket = new Socket();
    }

    public void connect(String address,int port) throws IOException {
        socket.connect(new InetSocketAddress(address,port));
    }

    public void close() throws IOException {
        socket.close();
    }

    public Socket getSocket() {
        return socket;
    }
}
