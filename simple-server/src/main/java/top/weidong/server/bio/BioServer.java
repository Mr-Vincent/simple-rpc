package top.weidong.server.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import static top.weidong.common.util.Preconditions.*;
/**
 * Created with IntelliJ IDEA.
 * Description:
 * BIO server 简单的基于传统io的服务端
 *
 * @author dongwei
 * @date 2018/03/12
 * Time: 21:33
 */
public class BioServer {

    /** 监听端口*/
    private static final int PORT = 8090;

    private ServerSocket ss;

    public BioServer() {
        try {
            this.ss = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException("服务初始化异常，请检查端口是否被占用");
        }
    }

    public BioServer(int port) {
        // check port is valid
        checkArgument(port>0,"端口号必须大于0");
        try {
            this.ss = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("服务初始化异常，请检查端口是否被占用");
        }
    }

    public void listen() throws IOException {
        Socket accept = ss.accept();
        InputStream inputStream = accept.getInputStream();
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);
        String nextLine = null;
        while ((nextLine = br.readLine()) != null){
            System.out.println(nextLine);
        }

    }
}
