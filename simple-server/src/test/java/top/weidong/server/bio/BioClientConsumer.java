package top.weidong.server.bio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/20
 * Time: 09:44
 */
public class BioClientConsumer {

    public static void main(String[] args) {
        BioClient bioClient = new BioClient();
        try {
            bioClient.connect("localhost",9999);
            ObjectOutputStream output = new ObjectOutputStream(bioClient.getSocket().getOutputStream());
            output.writeUTF("IHello");
            output.writeUTF("say");
            output.writeObject(new Class<?>[]{String.class});
            output.writeObject(new Object[]{"world"});

            ObjectInputStream input = new ObjectInputStream(bioClient.getSocket().getInputStream());
            Object result = input.readObject();
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bioClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
