package top.weidong.service.invoker;

import top.weidong.common.util.ExceptionUtil;
import top.weidong.common.util.Preconditions;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.network.protocal.SRequest;
import top.weidong.network.protocal.SResponse;
import top.weidong.serializer.SerializationFactory;
import top.weidong.serializer.Serializer;
import top.weidong.service.DefaultClient;
import top.weidong.service.Person;
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
        return (T) SimpleProxy.getProxy(clazz, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                // 将消息封装成对象
                SRequest request = new SRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(method.getDeclaringClass().getName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);

                Serializer jdkSerializer = SerializationFactory.getDefaultSerializer();
                byte[] bytes = jdkSerializer.writeObject(request);
                Socket socket = client.getSocket();
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();

                // 将字节写出去
                outputStream.write(bytes);
                LOGGER.debug("[{}]个字节待写出",bytes.length);
                // 不能再等着流可读，会一直阻塞住线程
                // 先将out关掉 这样就能将字节写出了
                socket.shutdownOutput();
                ByteArrayOutputStream tmp = new ByteArrayOutputStream();
                byte[] buffer = new byte[10];
                int length;
                try {
                    while ((length = inputStream.read(buffer)) != -1) {
                        tmp.write(buffer, 0, length);
                    }
                    tmp.flush();
                } catch (IOException e) {
                    ExceptionUtil.throwException(e);
                }finally {
                    close(socket,inputStream,outputStream);
                }
                byte[] result = tmp.toByteArray();
                LOGGER.debug("收到服务端的响应消息序列化完成，总字节长度为：[{}]字节",result.length);
                SResponse response = jdkSerializer.readObject(result, SResponse.class);
                LOGGER.debug("反序列化完成，反序列化后的对象：[{}]",response);
                return response.getResult();
            }
        });
    }

    /**
     * close all if not null
     * @param socket
     * @param input
     * @param output
     */
    private void close(Socket socket, InputStream input, OutputStream output){
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
}
