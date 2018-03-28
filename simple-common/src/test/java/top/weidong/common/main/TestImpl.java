package top.weidong.common.main;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/21
 * Time: 15:43
 */
public class TestImpl implements ITest {
    @Override
    public String say(String msg) {
        return "hello "+msg;
    }
}
