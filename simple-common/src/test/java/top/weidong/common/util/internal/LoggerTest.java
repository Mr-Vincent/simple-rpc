package top.weidong.common.util.internal;

import org.junit.Test;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/15
 * Time: 10:56
 */
public class LoggerTest extends AbsCommonTest{


    public static void main(String[] args) throws IOException {
//        /Library/Java/JavaVirtualMachines/jdk1.7.0_60.jdk/Contents/Home/jre/lib/log.properties
//        -Djava.util.logging.config.file=myfile

//        System.setProperty("java.util.logging.config.file","/Users/dongwei/log.properties");
//        String fname = System.getProperty("java.util.logging.config.file");
//        try {
//            InputStream in = new FileInputStream(fname);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        InternalLogger LOGGER = InternalLoggerFactory.getInstance(LoggerTest.class);
        LOGGER.trace("heheh");


    }

}
