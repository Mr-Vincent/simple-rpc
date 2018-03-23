package top.weidong.service.processor;

import top.weidong.common.util.IoUtil;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * Description: 输出到控制台的处理器
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 11:05
 */
public class SimpleConsoleProcessor extends AbstractProcessor{
    private static  final InternalLogger LOGGER = InternalLoggerFactory.getInstance(SimpleConsoleProcessor.class);

    @Override
    public void process(InputStream inputStream, OutputStream outputStream) {
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = null;
        try {
            while ((line = bufferedReader.readLine())!=null){
                LOGGER.debug(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(inputStream,outputStream);
        }
    }
}
