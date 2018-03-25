package top.weidong.service.processor;

import top.weidong.common.util.ExceptionUtil;
import top.weidong.common.util.IoUtil;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.network.protocal.SRequest;
import top.weidong.network.protocal.SResponse;
import top.weidong.serializer.SerializationFactory;
import top.weidong.serializer.Serializer;
import top.weidong.service.DefaultServer;
import top.weidong.service.SimpleContext;

import javax.sound.midi.SoundbankResource;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
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
     * 处理流  流的关闭一定要掌握好时机 不然一不小心就会报socket close异常
     * @param inputStream
     * @param outputStream
     */
    @Override
    public synchronized boolean  process(InputStream inputStream, OutputStream outputStream) {

        LOGGER.debug("【{}】开始处理消息=====>>>>>>",System.currentTimeMillis());

        Map<String,Class> serviceRegistry = SimpleContext.getServiceRegistry();
        // 获取字节 将其反序列化成对象 先读到tmp中 再将其转化为byte数组
        byte[] result = null;
        try {
            // 客户端多写了4字节作为消息长度 这里多读4个字节
            int readLength = IoUtil.readLength(inputStream);
            LOGGER.debug("【{}】本次需要读字节数[{}]=====>>>>>>",System.currentTimeMillis(),readLength);
            LOGGER.debug("【{}】准备读取字节=====>>>>>>",System.currentTimeMillis());
            result = IoUtil.readToBytes0(inputStream,readLength);
            if (result.length <=0 ){
                return false;
            }
        } catch (IOException e) {
            ExceptionUtil.throwException(e);
        }
        LOGGER.debug("收到客户端的请求消息序列化完成，总字节长度为：[{}]字节",result.length);

        Serializer jdkSerializer = SerializationFactory.getDefaultSerializer();
        SRequest sRequest = jdkSerializer.readObject(result, SRequest.class);
        String requestId = sRequest.getRequestId();
        String serviceName = sRequest.getClassName();
        String methodName = sRequest.getMethodName();
        Class<?>[] parameterTypes = sRequest.getParameterTypes();
        Object[] arguments = sRequest.getParameters();
        Class serviceClass = serviceRegistry.get(serviceName);

        SResponse response = new SResponse();
        try {
            if (serviceClass == null) {
                throw new ClassNotFoundException(serviceName + " not found");
            }
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            Object resultObj = method.invoke(serviceClass.newInstance(), arguments);
            LOGGER.debug("响应结果为：[{}]",resultObj);
            // 与请求消息id对应
            response.setRequestId(requestId);
            response.setResult(resultObj);
            byte[] writeObject = jdkSerializer.writeObject(response);
            LOGGER.debug("消息处理结束，响应消息序列化完成，总字节长度为：[{}]",writeObject.length);
            // 最后将其输出
            IoUtil.writeLength(outputStream,writeObject.length);
            outputStream.write(writeObject);
        } catch (Exception e) {
            response.setError(e.getLocalizedMessage());
            ExceptionUtil.throwException(e);
        } finally {
            //IoUtil.close(inputStream,outputStream);
        }
        return true;

    }
}
