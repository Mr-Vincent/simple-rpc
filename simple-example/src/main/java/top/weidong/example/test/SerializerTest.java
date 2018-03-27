package top.weidong.example.test;

import top.weidong.serializer.SerializationFactory;
import top.weidong.serializer.Serializer;
import top.weidong.serializer.enums.SerializerType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/27
 * Time: 15:37
 */
public class SerializerTest {

    public static void main(String[] args) {
        Serializer serializer = SerializationFactory.create(SerializerType.PROTO_STUFF);
        ResultWrapper wrapper = new ResultWrapper();
        wrapper.setResult("test");
        wrapper.setError(new RuntimeException("test"));
        wrapper.setClazz(new Class[] { String.class, ArrayList.class, Serializable.class });
        byte[] bytes = serializer.writeObject(wrapper);
        ResultWrapper wrapper1 = serializer.readObject(bytes, ResultWrapper.class);
        wrapper1.getError().printStackTrace();
        System.out.println(bytes.length);
        System.out.println(wrapper1.getResult());
        // noinspection ImplicitArrayToString
        System.out.println(wrapper1.getClazz());

    }
}

class ResultWrapper implements Serializable {

    private static final long serialVersionUID = -1126932930252953428L;

    private Object result; // 服务调用结果
    private Exception error; // 错误信息
    private Class<?>[] clazz;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    public Class<?>[] getClazz() {
        return clazz;
    }

    public void setClazz(Class<?>[] clazz) {
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return "ResultWrapper{" +
                "result=" + result +
                ", error=" + error +
                '}';
    }
}
