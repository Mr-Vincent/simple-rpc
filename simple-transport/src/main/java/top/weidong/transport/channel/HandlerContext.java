package top.weidong.transport.channel;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/28
 * Time: 17:59
 */
public interface HandlerContext {
    SimpleChannel channel();

    HandlerContext fireChannelActive();

    HandlerContext fireChannelInactive();
}
