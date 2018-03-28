package top.weidong.service.proxy;


import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static top.weidong.common.util.Preconditions.checkArgument;

/**
 * Created with IntelliJ IDEA.
 * Description: 代理
 *
 * @author dongwei
 * @date 2018/03/28
 * Time: 17:09
 */
public enum Proxies {
    /** jdk代理实现*/
    JDK_PROXY(new ProxyDelegate() {
        @Override
        public <T> T newProxy(Class<T> interfaceType, Object handler) {
            checkArgument(handler instanceof InvocationHandler, "handler must be a InvocationHandler");
            Object object = Proxy.newProxyInstance(
                    interfaceType.getClassLoader(), new Class<?>[] { interfaceType }, (InvocationHandler) handler);
            return interfaceType.cast(object);
        }
    }),
    ASSIT(new ProxyDelegate() {
        @Override
        public <T> T newProxy(Class<T> interfaceType, Object handler) {
            checkArgument(handler instanceof MethodHandler, "handler must be a MethodHandler");
            ProxyFactory proxyFactory = new ProxyFactory();
            proxyFactory.setInterfaces(new Class[]{interfaceType});
            Class c = proxyFactory.createClass();
            Object result = null;
            try {
                result = c.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((ProxyObject)result).setHandler((MethodHandler)handler);
            return interfaceType.cast(result);
        }
    }),
    CGLIB(new ProxyDelegate() {
        @Override
        public <T> T newProxy(Class<T> interfaceType, Object handler) {
            checkArgument(handler instanceof MethodInterceptor, "handler must be a MethodInterceptor");
            Enhancer enhancer = new Enhancer();
            enhancer.setCallback((Callback) handler);
            enhancer.setInterfaces(new Class[] { interfaceType });
            Object result = enhancer.create();
            return interfaceType.cast(result);
        }
    });

    private ProxyDelegate delegate;

    Proxies(ProxyDelegate delegate) {
        this.delegate = delegate;
    }

    public <T> T newProxy(Class<T> interfaceType,Object handler){
        return delegate.newProxy(interfaceType,handler);
    }



    interface ProxyDelegate {

        /**
         * Returns a proxy instance that implements {@code interfaceType} by dispatching
         * method invocations to {@code handler}. The class loader of {@code interfaceType}
         * will be used to define the proxy class.
         */
        <T> T newProxy(Class<T> interfaceType, Object handler);
    }
}
