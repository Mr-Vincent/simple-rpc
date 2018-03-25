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
        byte[] buffer = new byte[10];
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
     * 一个字节的写
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readToBytes0(InputStream inputStream) throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        int b;
        try {
            while ((b = inputStream.read()) != -1) {
                tmp.write(b);
            }
            tmp.flush();
        } finally {
            tmp.close();
        }
        return tmp.toByteArray();
    }

    /**
     * 读指定长度的字节 读完就完事
     *
     * @param inputStream
     * @param length
     * @return
     * @throws IOException
     */
    public static byte[] readToBytes0(InputStream inputStream, int length) throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        int counter = 0;
        int b;
        try {
            while (counter < length) {
                b = inputStream.read();
                tmp.write(b);
                counter++;
            }
            tmp.flush();
        } finally {
            tmp.close();
        }
        return tmp.toByteArray();
    }

    /**
     * 关闭流 三个参数
     *
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
     *
     * @param input
     * @param output
     */
    public static void close(InputStream input, OutputStream output) {
        close(null, input, output);
    }

    /**
     * 关闭流 1个参数
     *
     * @param input
     */
    public static void close(InputStream input) {
        close(null, input, null);
    }

    /**
     * 关闭流 1个参数
     *
     * @param output
     */
    public static void close(OutputStream output) {
        close(null, null, output);
    }

    /**
     * 在流中写要输出的字节长度
     * @param outputStream
     * @param byteLength
     */
    public static void writeLength(OutputStream outputStream, int byteLength) throws IOException {
        byte b0 = (byte) (byteLength >>> 24);
        byte b1 = (byte) (byteLength >>> 16);
        byte b2 = (byte) (byteLength >>> 8);
        byte b3 = (byte) byteLength;

        outputStream.write(b0);
        outputStream.write(b1);
        outputStream.write(b2);
        outputStream.write(b3);

    }

    /**
     * 在流中读要读取的字节长度
     * @param inputStream
     * @return
     */
    public static int readLength(InputStream inputStream) throws IOException {
        int b0 = inputStream.read();
        int b1 = inputStream.read();
        int b2 = inputStream.read();
        int b3 = inputStream.read();
        int high = b0 << 24;
        int mid = b1 << 16;
        int mi = b2 << 8;
        return high + mid + mi + b3;
    }
}
