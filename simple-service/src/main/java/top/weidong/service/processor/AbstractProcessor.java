package top.weidong.service.processor;

import top.weidong.network.processor.Processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 10:43
 */
public abstract class AbstractProcessor implements Processor{

    /**
     * 关闭流操作
     * @param input
     * @param output
     */
    protected void close(InputStream input, OutputStream output){
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
}
