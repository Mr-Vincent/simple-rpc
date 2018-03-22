package top.weidong.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: 上下文
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 10:14
 */
public class SimpleContext {

    private static final Map<String, Class> SERVICE_REGISTRY = new HashMap<String, Class>();

    public static Map<String, Class> getServiceRegistry() {
        return SERVICE_REGISTRY;
    }
}
