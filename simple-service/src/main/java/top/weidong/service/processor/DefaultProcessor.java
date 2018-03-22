package top.weidong.service.processor;

import top.weidong.common.util.internal.InternalLogger;
import top.weidong.common.util.internal.InternalLoggerFactory;
import top.weidong.service.DefaultServer;
import top.weidong.service.SimpleContext;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: 默认处理 耦合了序列化代码
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 10:42
 */
public class DefaultProcessor extends AbstractProcessor{

    private final static InternalLogger LOGGER = InternalLoggerFactory.getInstance(DefaultServer.class);


    /**
     * 处理流
     * @param inputStream
     * @param outputStream
     */
    @Override
    public void process(InputStream inputStream, OutputStream outputStream) {
        Map<String,Class> serviceRegistry = SimpleContext.getServiceRegistry();

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
            close(input,output);
        }
    }
}
