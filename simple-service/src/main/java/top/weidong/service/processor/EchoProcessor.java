package top.weidong.service.processor;

import top.weidong.common.util.IoUtil;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * Description: 回显processor
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 18:50
 */
public class EchoProcessor extends AbstractProcessor{
    private static  final InternalLogger LOGGER = InternalLoggerFactory.getInstance(EchoProcessor.class);

    @Override
    public void process(InputStream inputStream, OutputStream outputStream) {

        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        String line = null;
        try {
            while ((line = bufferedReader.readLine())!=null){
                bufferedWriter.write(line);
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(inputStream,outputStream);
        }
    }
}
