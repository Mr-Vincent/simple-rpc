package top.weidong.service.handler;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/29
 * Time: 13:27
 */

import javassist.util.proxy.MethodHandler;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import top.weidong.common.util.ExceptionUtil;
import top.weidong.common.util.IoUtil;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.common.util.lock.MutexLock;
import too.weidong.network.bio.protocal.SRequest;
import too.weidong.network.bio.protocal.SResponse;
import top.weidong.serializer.SerializationFactory;
import top.weidong.serializer.Serializer;
import top.weidong.service.DefaultClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.UUID;

public class TransferHandler implements InvocationHandler, MethodHandler, MethodInterceptor {

    private final static InternalLogger LOGGER = InternalLoggerFactory.getInstance(TransferHandler.class);

    private MutexLock lock;
    private DefaultClient client;
    private Socket socket = null;

    public TransferHandler(DefaultClient client, MutexLock lock) {
        this.lock = lock;
        this.client = client;
        socket = client.getSocket();
    }

    private Socket getSocket() {
        if (socket == null) {
            socket = client.getSocket();
        }
        return socket;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        lock.lock();
        try {
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
            IoUtil.writeLength(outputStream, writeLength);
            // 将字节写出去
            outputStream.write(bytes);
            // 写结束标记
            LOGGER.debug("[{}]个字节待写出", writeLength);
            byte[] result = null;
            try {
                int readLength = IoUtil.readLength(inputStream);
                result = IoUtil.readToBytes0(inputStream, readLength);
            } catch (IOException e) {
                ExceptionUtil.throwException(e);
            } finally {
                //IoUtil.close(socket,inputStream,outputStream);
            }
            LOGGER.debug("收到服务端的响应消息序列化完成，总字节长度为：[{}]字节", result.length);
            SResponse response = jdkSerializer.readObject(result, SResponse.class);
            LOGGER.debug("反序列化完成，反序列化后的对象：[{}]", response);
            return response.getResult();
        } finally {
            lock.unlock();
        }

    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return invoke(o, method, objects);
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        return invoke(self, thisMethod, args);
    }

    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
        return invoke(proxy, method, args);
    }
}
