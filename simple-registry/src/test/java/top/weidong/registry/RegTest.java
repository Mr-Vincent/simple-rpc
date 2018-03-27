package top.weidong.registry;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/27
 * Time: 21:16
 */
public class RegTest {

    public static void main(String[] args) throws IOException {
        SimpleRegistry simpleRegistry = new SimpleRegistry();
        simpleRegistry.register("123456");
        System.in.read();
    }
}
