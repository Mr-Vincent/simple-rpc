package top.weidong.common.util.internal;

import org.junit.After;
import org.junit.Before;

/**
 * Created with IntelliJ IDEA.
 * Description: 抽象test
 *
 * @author dongwei
 * @date 2018/03/15
 * Time: 10:54
 */
public abstract class AbsCommonTest {

    @Before
    public void before(){
        System.out.println("before");
    }


    @After
    public void after(){
        System.out.println("after");
    }
}
