package top.weidong.server.bio;

import org.junit.Test;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * BIO server 简单的基于传统io的服务端
 *
 * @author dongwei
 * @date 2018/03/12
 * Time: 21:33
 */
public class BioServerTest {

    @Test
    public void testServer(){
        BioServer bioServer = new BioServer(9999);
        try {
            bioServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
