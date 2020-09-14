package top.weidong.network.bio;

import org.junit.Test;
import too.weidong.network.bio.SimpleBioAcceptor;
import top.weidong.network.api.Acceptor;

import java.io.IOException;

/**
 * @author dongwei
 * @since 2020/09/14
 * Time: 15:40
 */
public class AcceptorTest {


    @Test
    public void testAcceptor() throws IOException {
        Acceptor acceptor = new SimpleBioAcceptor(9999);
        acceptor.start();
        System.in.read();

    }
}
