package top.weidong.example.designpattern.cor;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/28
 * Time: 11:19
 */
public interface FilterChain {
    void handler(Context ctx);

    void fireNext(Context ctx);
}
