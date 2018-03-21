package top.weidong.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Load resources (or images) from various sources.
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 15:16
 */
public final class Loader {

    static {
        LogLog.setInternalDebugging(true);
    }


    /**
     * get thread classloader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static ClassLoader getTCL() throws IllegalAccessException,
            InvocationTargetException {
        // Are we running on a JDK 1.2 or later system?
        Method method = null;
        try {
            method = Thread.class.getMethod("getContextClassLoader", null);
        } catch (NoSuchMethodException e) {
            // We are running on JDK 1.1
            return null;
        }
        return (ClassLoader) method.invoke(Thread.currentThread(), null);
    }


    /**
     * find a resource by name
     * @param resourceName
     * @return URL
     */
    public static URL getResource(String resourceName){
        ClassLoader classLoader = null;
        URL url = null;
        try {
            classLoader = getTCL();
            if(classLoader != null) {
                url = classLoader.getResource(resourceName);
                if(url != null) {
                    return url;
                }
            }
        } catch (IllegalAccessException e) {
            LogLog.error(e.getMessage());
        } catch (InvocationTargetException e) {
            LogLog.error(e.getMessage());
        }
        // 没有找到：1.cl不存在 2.资源不存在
        classLoader = Loader.class.getClassLoader();
        if(classLoader != null) {
            url = classLoader.getResource(resourceName);
            if(url != null) {
                return url;
            }
        }
        return ClassLoader.getSystemResource(resourceName);
    }
}
