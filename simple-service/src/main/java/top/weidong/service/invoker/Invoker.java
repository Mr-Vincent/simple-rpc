package top.weidong.service.invoker;

import top.weidong.common.util.ExceptionUtil;
import top.weidong.common.util.IoUtil;
import top.weidong.common.util.Preconditions;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.network.protocal.SRequest;
import top.weidong.network.protocal.SResponse;
import top.weidong.serializer.SerializationFactory;
import top.weidong.serializer.Serializer;
import top.weidong.service.DefaultClient;
import top.weidong.service.proxy.SimpleProxy;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description: 代理调用器--很直接的翻译😄
 * 暴露太多细节 不够抽象 对以后的拓展很不方便
 * i/o流的细节应该抽象出来
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 18:01
 */
public class Invoker {

    private final static InternalLogger LOGGER = InternalLoggerFactory.getInstance(Invoker.class);

    private DefaultClient client;

    public Invoker(DefaultClient client) {
        this.client = client;
    }

    public <T> T invoke(final Class<T> clazz){
        Preconditions.checkNotNull(client);
        return (T) SimpleProxy.getProxy(clazz, new TransferHandler(client));
    }

    /**
     * customer handler implementation
     */
    static class TransferHandler implements InvocationHandler{
        DefaultClient client;
        Socket socket = null;
        TransferHandler(DefaultClient client){
            this.client = client;
            socket = client.getSocket();
        }
        private Socket getSocket(){
            if (socket == null) {
                socket = client.getSocket();
            }
            return socket;
        }
        @Override
        public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 将消息封装成对象
            SRequest request = new SRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);

            Serializer jdkSerializer = SerializationFactory.getDefaultSerializer();
            byte[] bytes = jdkSerializer.writeObject(request);
            Socket socket = getSocket();
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            int writeLength = bytes.length;
            // write只能写byte 将int拆为4个byte顺序写 服务端顺序接收再拼接成int即可
            IoUtil.writeLength(outputStream,writeLength);
            // 将字节写出去
            outputStream.write(bytes);
            // 写结束标记
            LOGGER.debug("[{}]个字节待写出",bytes.length);
            byte[] result = null;
            try {
                int readLength = IoUtil.readLength(inputStream);
                result = IoUtil.readToBytes0(inputStream,readLength);
            } catch (IOException e) {
                ExceptionUtil.throwException(e);
            }finally {
                //IoUtil.close(socket,inputStream,outputStream);
            }
            LOGGER.debug("收到服务端的响应消息序列化完成，总字节长度为：[{}]字节",result.length);
            SResponse response = jdkSerializer.readObject(result, SResponse.class);
            LOGGER.debug("反序列化完成，反序列化后的对象：[{}]",response);
            return response.getResult();
        }
    }
}
