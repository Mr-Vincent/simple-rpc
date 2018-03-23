package top.weidong.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description: 处理io
 *
 * @author dongwei
 * @date 2018/03/23
 * Time: 09:13
 */
public abstract class IoUtil {


    /**
     * 将io流读成字节
     *
     * @param inputStream
     * @return
     */
    public static byte[] readToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                tmp.write(buffer, 0, length);
            }
            tmp.flush();
        } finally {
            tmp.close();
        }
        return tmp.toByteArray();
    }

    /**
     * 关闭流 三个参数
     * @param socket
     * @param input
     * @param output
     */
    public static void close(Socket socket, InputStream input, OutputStream output) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            ExceptionUtil.throwException(e);
        }
    }

    /**
     * 关闭流 2个参数
     * @param input
     * @param output
     */
    public static void close(InputStream input, OutputStream output){
        close(null,input,output);
    }

    /**
     * 关闭流 1个参数
     * @param input
     */
    public static void close(InputStream input){
        close(null,input,null);
    }

    /**
     * 关闭流 1个参数
     * @param output
     */
    public static void close(OutputStream output){
        close(null,null,output);
    }
}
