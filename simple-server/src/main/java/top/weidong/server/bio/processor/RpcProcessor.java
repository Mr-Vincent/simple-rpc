package top.weidong.server.bio.processor;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: 远程调用处理器
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 17:34
 */
public class RpcProcessor {


    private Map<String,Class> serviceRegistry;

    public RpcProcessor(Map<String,Class> serviceRegistry){
        this.serviceRegistry = serviceRegistry;
    }
    /**
     * 处理流
     * @param inputStream
     * @param outputStream
     * @return
     */
    public Object process(InputStream inputStream, OutputStream outputStream){
        ObjectInputStream input = null;
        ObjectOutputStream output = null;
        try {
            input = new ObjectInputStream(inputStream);
            output = new ObjectOutputStream(outputStream);
            String serviceName = input.readUTF();
            String methodName = input.readUTF();
            Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
            Object[] arguments = (Object[]) input.readObject();
            Class serviceClass = serviceRegistry.get(serviceName);
            if (serviceClass == null) {
                throw new ClassNotFoundException(serviceName + " not found");
            }
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            Object result = method.invoke(serviceClass.newInstance(), arguments);

            output.writeObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
