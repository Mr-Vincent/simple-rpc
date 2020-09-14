package top.weidong.service.invoker;

import top.weidong.common.util.Preconditions;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.common.util.lock.MutexLock;
import top.weidong.service.DefaultClient;
import top.weidong.service.handler.TransferHandler;
import top.weidong.service.proxy.Proxies;

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
