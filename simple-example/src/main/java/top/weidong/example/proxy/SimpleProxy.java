package top.weidong.example.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * Description: 简单代理实现
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 15:58
 */
public class SimpleProxy<T> {


    /**
     * 通过代理创建对象
     * @param clazz 接口
     * @param target 接口实现
     * @param <T> 泛型
     * @return
     */
    public static  <T> T getProxy(Class<T> clazz,Object target){
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{clazz},new MyInvocationHandler(target));
    }


    /**
     * 不需要实现类的代理方法
     * @param clazz
     * @param handler
     * @param <T>
     * @return
     */
    public static  <T> T getProxy(Class<T> clazz,InvocationHandler handler){
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{clazz},handler);
    }

    static class MyInvocationHandler implements InvocationHandler {
        private Object target;
        MyInvocationHandler(Object target){
            this.target = target;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(target, args);
            return result;
        }
    }
}
