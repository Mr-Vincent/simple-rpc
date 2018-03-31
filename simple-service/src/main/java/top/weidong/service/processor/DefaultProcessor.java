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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: 默认处理 耦合了序列化代码
 * 反射调用 服务端完成后将结果字节码返回客户端
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 10:42
 */
public class DefaultProcessor extends AbstractProcessor {

    private final static InternalLogger LOGGER = InternalLoggerFactory.getInstance(DefaultServer.class);

    private Map<String,Object> handlerMap;

    public DefaultProcessor(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 处理流  流的关闭一定要掌握好时机 不然一不小心就会报socket close异常
     *
     * @param inputStream
     * @param outputStream
     */
    @Override
    public synchronized boolean process(InputStream inputStream, OutputStream outputStream) {

        LOGGER.debug("【{}】开始处理消息=====>>>>>>", System.currentTimeMillis());

        byte[] result = null;
        try {
            // 客户端多写了4字节作为消息长度 这里多读4个字节
            int readLength = IoUtil.readLength(inputStream);
            if (readLength <= 0) {
                return false;
            }
            LOGGER.debug("【{}】本次需要读字节数[{}]个=====>>>>>>", System.currentTimeMillis(), readLength);
            LOGGER.debug("【{}】准备读取字节=====>>>>>>", System.currentTimeMillis());
            result = IoUtil.readToBytes0(inputStream, readLength);
            LOGGER.debug("收到客户端的请求消息序列化完成，总字节长度为：[{}]个字节", result.length);
        } catch (IOException e) {
            ExceptionUtil.throwException(e);
        }

        Serializer jdkSerializer = SerializationFactory.getDefaultSerializer();
        SRequest sRequest = jdkSerializer.readObject(result, SRequest.class);
        String requestId = sRequest.getRequestId();
        String serviceName = sRequest.getClassName();
        String methodName = sRequest.getMethodName();
        Class<?>[] parameterTypes = sRequest.getParameterTypes();
        Object[] arguments = sRequest.getParameters();

        Object serviceBean = handlerMap.get(serviceName);
        Class<?> serviceClass = serviceBean.getClass();

        SResponse response = new SResponse();
        try {
            if (serviceClass == null) {
                throw new ClassNotFoundException(serviceName + " not found");
            }
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            Object resultObj = method.invoke(serviceBean, arguments);
            LOGGER.debug("响应结果为：[{}]", resultObj);
            // 与请求消息id对应
            response.setRequestId(requestId);
            response.setResult(resultObj);
            byte[] writeObject = jdkSerializer.writeObject(response);
            LOGGER.debug("消息处理结束，响应消息序列化完成，总字节长度为：[{}]", writeObject.length);
            // 最后将其输出
            IoUtil.writeLength(outputStream, writeObject.length);
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
