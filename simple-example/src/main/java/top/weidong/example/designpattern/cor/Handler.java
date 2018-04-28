package top.weidong.example.designpattern.cor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/28
 * Time: 11:18
 */
public interface Handler {

    void handle(Context ctx,FilterChain filterChain);
}
