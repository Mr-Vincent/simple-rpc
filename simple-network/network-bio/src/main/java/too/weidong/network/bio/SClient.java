package too.weidong.network.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description: simple client
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 09:30
 */
public class SClient {

    private Socket socket = null;

    public SClient(String address,int port) throws IOException {
        socket = new Socket(address,port);
    }

    public SClient() {
        socket = new Socket();
    }

    public SClient connect(String address,int port) throws IOException {
        socket.connect(new InetSocketAddress(address,port));
        return this;
    }

    public SClient connect(String address,int port,int timeout) throws IOException {
        socket.connect(new InetSocketAddress(address,port),timeout);
        return this;
    }

    public void close() throws IOException {
        socket.close();
    }

    public Socket getSocket() {
        return socket;
    }
}
