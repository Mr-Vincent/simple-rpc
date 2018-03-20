package top.weidong.server.bio;

import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * BIO server 简单的基于传统io的服务端
 *
 * @author dongwei
 * @date 2018/03/12
 * Time: 21:33
 */
public class BioServerProvider {

    public static void main(String[] args) throws IOException {
        BioServer bioServer = new BioServer(9999);
        try {
            bioServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = bioServer.getClient().getInputStream();
        OutputStream outputStream = bioServer.getClient().getOutputStream();
        try {
            ObjectInputStream input = new ObjectInputStream(inputStream);

            String serviceName = input.readUTF();
            String methodName = input.readUTF();
            Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
            Object[] arguments = (Object[]) input.readObject();
            Class serviceClass = HelloImpl.class;
            if (serviceClass == null) {
                throw new ClassNotFoundException(serviceName + " not found");
            }
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            Object result = method.invoke(serviceClass.newInstance(), arguments);

            ObjectOutputStream output = new ObjectOutputStream(outputStream);
            output.writeObject(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bioServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
