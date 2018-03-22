package top.weidong.service.processor;

import top.weidong.network.enums.ProcessorType;
import top.weidong.network.processor.Processor;

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
     *
     * @param type
     * @return
     */
    public static Processor newInstance(ProcessorType type) {
        if (ProcessorType.getDefault().equals(type)) {
            return new DefaultProcessor();
        } else if (ProcessorType.CONSOLE.equals(type)) {
            return new SimpleConsoleProcessor();
        } else if (ProcessorType.ECHO.equals(type)) {
            return new EchoProcessor();
        } else {
            throw new RuntimeException("无法识别的类型！");
        }
    }
}
