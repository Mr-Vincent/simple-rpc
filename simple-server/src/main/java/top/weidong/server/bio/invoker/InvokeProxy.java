package top.weidong.server.bio.invoker;

import top.weidong.common.util.Preconditions;
import top.weidong.server.bio.BioClient;
import top.weidong.server.bio.proxy.SimpleProxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description: 代理调用器--很直接的翻译😄
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 18:01
 */
public class InvokeProxy {

    private BioClient client;

    public InvokeProxy(BioClient client) {
        this.client = client;
    }

    public <T> T invoke(final Class<T> clazz){
        Preconditions.checkNotNull(client);
        return (T) SimpleProxy.getProxy(clazz, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = client.getSocket();
                ObjectOutputStream output = null;
                ObjectInputStream input = null;
                try {
                    output = new ObjectOutputStream(socket.getOutputStream());
                    output.writeUTF(clazz.getName());
                    output.writeUTF(method.getName());
                    output.writeObject(method.getParameterTypes());
                    output.writeObject(args);

                    input = new ObjectInputStream(socket.getInputStream());

                    return input.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
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
                return null;
            }
        });
    }
}
