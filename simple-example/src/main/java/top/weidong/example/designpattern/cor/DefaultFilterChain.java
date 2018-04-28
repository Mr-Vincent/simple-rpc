package top.weidong.example.designpattern.cor;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/28
 * Time: 11:20
 */
public class DefaultFilterChain implements FilterChain {
    private FilterChain next;
    private Handler handler;

    public DefaultFilterChain(FilterChain next, Handler handler) {
        this.next = next;
        this.handler = handler;
    }

    @Override
    public void handler(Context ctx) {
        handler.handle(ctx,this);
    }

    @Override
    public void fireNext(Context ctx) {
        FilterChain next_ = this.next;
        if(next_ != null){
            next_ .handler(ctx);
        }
    }
}
