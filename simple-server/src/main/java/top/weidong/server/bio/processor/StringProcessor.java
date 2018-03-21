package top.weidong.server.bio.processor;

import top.weidong.common.util.internal.InternalLogger;
import top.weidong.common.util.internal.InternalLoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * Description: 对字符串简单处理
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 16:30
 */
public class StringProcessor {

    InternalLogger logger = InternalLoggerFactory.getInstance(StringProcessor.class);

    public Object process(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = null;
        while ((line = bufferedReader.readLine())!=null){
            sb.append(line);
            logger.debug(line);
        }
        return sb.toString();
    }

}
