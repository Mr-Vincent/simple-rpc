package top.weidong.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/07/27
 * Time: 15:25
 */
public class ApplicationMain {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:my.xml");
        MySimple mySimple = ctx.getBean(MySimple.class);
        String currentTime = mySimple.print();
        System.out.println(currentTime);
        System.out.println(mySimple.flag());

        InitializingBeanTest initializingBeanTest = ctx.getBean(InitializingBeanTest.class);
        System.out.println(initializingBeanTest);
    }
}
