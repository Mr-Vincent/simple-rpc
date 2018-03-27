package top.weidong.serializer.jdk;

import top.weidong.common.util.ExceptionUtil;
import top.weidong.serializer.Serializer;
import top.weidong.serializer.enums.SerializerType;

import java.io.*;


/**
 * Created with IntelliJ IDEA.
 * Description: Jdk自带的序列化
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 16:08
 */
public class JdkSerializer extends Serializer {

    /** 目的是复用 ByteArrayOutputStream 中的 byte[]*/
    private static final ThreadLocal<ByteArrayOutputStream> bufThreadLocal = new ThreadLocal<ByteArrayOutputStream>() {
        @Override
        protected ByteArrayOutputStream initialValue() {
            return new ByteArrayOutputStream(DEFAULT_BUF_SIZE);
        }
    };


    @Override
    public byte code() {
        return SerializerType.JAVA.value();
    }

    /**
     * 将对象写成字节数组
     * @param obj
     * @param <T>
     * @return
     */
    @Override
    public <T> byte[] writeObject(T obj) {
        ByteArrayOutputStream buf = bufThreadLocal.get();
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(buf);
            outputStream.writeObject(obj);
            outputStream.flush();
            return buf.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionUtil.throwException(e);
        }finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                    // never go here
                }
            }
            buf.reset(); // for reuse
        }
        return null;
    }

    /**
     * 将字节读成对象
     * @param bytes
     * @param offset
     * @param length
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T readObject(byte[] bytes, int offset, int length, Class<T> clazz) {
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes,offset,length));
            Object obj = inputStream.readObject();
            return clazz.cast(obj);
        } catch (Exception e) {
            ExceptionUtil.throwException(e);
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {}
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "java:(code=" + code() + ")";
    }
}
