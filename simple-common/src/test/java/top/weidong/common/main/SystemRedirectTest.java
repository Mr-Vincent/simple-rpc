package top.weidong.common.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created with IntelliJ IDEA.
 * Description: sout 的重定向
 *
 * @author dongwei
 * @date 2018/03/15
 * Time: 14:07
 */
public class SystemRedirectTest {
    private static void redirectSystemOut(){
        try {
            String path = SystemRedirectTest.class.getClass().getResource("/").getPath();
            // 将输出流定义到一个文件中
            PrintStream ps = new PrintStream(new FileOutputStream(path+"/system_out.txt"));
            System.setOut(ps);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("out put test====>");
        System.out.println("out put test====>");
        System.out.println("out put test====>");
        System.out.println("out put test====>");
    }

    public static void main(String[] args) {
        redirectSystemOut();
    }
}
