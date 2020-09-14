package top.weidong.service.processor;

import network.enums.ProcessorType;
import too.weidong.network.bio.processor.Processor;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: processor 工厂类
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 10:50
 */
public class ProcessorFactory {

    private ProcessorFactory() {
    }
    /**
     * 获取实例
     * @param type
     * @param handler
     * @return
     */
    public static Processor newInstance(ProcessorType type, Map<String,Object> handler) {
        if (ProcessorType.getDefault().equals(type)) {
            return new DefaultProcessor(handler);
        } else if (ProcessorType.CONSOLE.equals(type)) {
            return new SimpleConsoleProcessor();
        } else if (ProcessorType.ECHO.equals(type)) {
            return new EchoProcessor();
        } else {
            throw new RuntimeException("无法识别的类型！");
        }
    }
}
