package too.weidong.network.bio;

import top.weidong.network.api.ServerSocketFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author dongwei
 * @since 2020/09/14
 * Time: 20:46
 */
public class DefaultServerSocketFactory implements ServerSocketFactory {
    @Override
    public ServerSocket createSocket(int port, int backlog) throws IOException {
        return new ServerSocket(port,backlog);
    }

    @Override
    public ServerSocket createSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    @Override
    public ServerSocket createSocket(int port, int backlog, InetAddress ifAddress) throws IOException {
        return new ServerSocket(port,backlog,ifAddress);
    }

    @Override
    public Socket acceptSocket(ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }
}
