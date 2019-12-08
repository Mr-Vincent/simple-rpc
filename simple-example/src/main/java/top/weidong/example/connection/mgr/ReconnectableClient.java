package top.weidong.example.connection.mgr;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @author dongwei
 * @since 2019/11/28
 * Time: 17:17
 * 可自动重连的客户端
 */
public class ReconnectableClient {
    private static final String HOST="127.0.0.1";

    private static final int PORT = 9999;

    private Socket socket;

    public void run(boolean reconnect) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(HOST,PORT));

        new Keepalived(reconnect,socket).start();
        readMsg(socket);
    }

    private void readMsg(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedInputStream br = new BufferedInputStream(inputStream);
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = br.read(buf)) !=-1){
            System.out.println(len);
        }
    }

    public static void main(String[] args) throws IOException {
        new ReconnectableClient().run(true);
    }

    private class Keepalived extends Thread{
        boolean needReconnect;
        Socket socket;
        public Keepalived(boolean needReconnect,Socket socket){
            this.needReconnect = needReconnect;
            this.socket = socket;
        }
        @Override
        public void run() {
            System.out.println("检测客户端是否断线......");
            boolean closed = false;

            for (;;) {
                if(needReconnect){
                    try {
                        socket.sendUrgentData(0xff);
                    } catch (IOException e) {
                        closed = true;
                    }
                    System.out.println("当前客户端是否掉线："+closed);
                    if(closed){
                        socket = new Socket();
                        try {
                            socket.connect(new InetSocketAddress(HOST,PORT));
                        } catch (IOException e) {
                            System.out.println("重连失败"+e.getMessage());
                        }
                    }
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
