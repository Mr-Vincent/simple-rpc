package top.weidong.service.processor;

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
     *
     * @param handler
     * @return
     */
    public static Processor newInstance(Map<String, Object> handler) {
        return new DefaultProcessor(handler);
    }
}
