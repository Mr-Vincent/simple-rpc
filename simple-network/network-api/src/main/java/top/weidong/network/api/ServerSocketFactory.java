package top.weidong.network.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author dongwei
 * @since 2020/09/14
 * Time: 20:41
 * socket 工厂
 */
public interface ServerSocketFactory {

    /**
     * create ServerSocket
     * @param port
     * @param backlog
     * @return
     */
    ServerSocket createSocket(int port,int backlog) throws IOException ;


    /**
     * create ServerSocket
     * @param port
     * @return
     */
    ServerSocket createSocket(int port)  throws IOException ;

    /**
     * create ServerSocket
     * @param port
     * @param backlog
     * @param ifAddress
     * @return
     */
    ServerSocket createSocket(int port, int backlog, InetAddress ifAddress)  throws IOException ;

    /**
     * accept a socket
     * @param serverSocket
     * @return
     * @throws IOException
     */
    Socket acceptSocket(ServerSocket serverSocket) throws IOException;


}
