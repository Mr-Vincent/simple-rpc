package top.weidong.network.processor;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * Description: 逻辑处理器 接口
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 09:42
 */
public interface Processor {


    /**
     * 处理输入和输出流
     * @param inputStream
     * @param outputStream
     */
    void process(InputStream inputStream, OutputStream outputStream);
}
