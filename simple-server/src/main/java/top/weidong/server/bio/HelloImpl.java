package top.weidong.server.bio;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/20
 * Time: 09:29
 */
public class HelloImpl implements IHello {
    @Override
    public String say(String msg) {
        return "hello "+ msg;
    }
}
