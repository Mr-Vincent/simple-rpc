package top.weidong.service.invoker;

import javassist.util.proxy.MethodHandler;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import top.weidong.common.util.ExceptionUtil;
import top.weidong.common.util.IoUtil;
import top.weidong.common.util.Preconditions;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.common.util.lock.MutexLock;
import top.weidong.network.protocal.SRequest;
import top.weidong.network.protocal.SResponse;
import top.weidong.serializer.SerializationFactory;
import top.weidong.serializer.Serializer;
import top.weidong.service.DefaultClient;
import top.weidong.service.handler.TransferHandler;
import top.weidong.service.proxy.Proxies;
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

    private MutexLock lock = new MutexLock();

    public Invoker(DefaultClient client) {
        this.client = client;
    }

    public <T> T invoke(final Class<T> clazz){
        Preconditions.checkNotNull(client);
        return Proxies.BYTE_BUDDY_PROXY.newProxy(clazz,new TransferHandler(client,lock));
//        return (T) SimpleProxy.getProxy(clazz, new TransferHandler(client,lock));
    }

}
