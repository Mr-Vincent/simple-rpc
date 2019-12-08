package top.weidong.spring;

import org.springframework.beans.factory.InitializingBean;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/07/27
 * Time: 16:51
 */
public class InitializingBeanTest implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("~~~~~~~~afterPropertiesSet~~~~~~~");
    }
}
