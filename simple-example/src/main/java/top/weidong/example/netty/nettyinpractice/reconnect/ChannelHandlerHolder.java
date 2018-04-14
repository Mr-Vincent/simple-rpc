package top.weidong.example.netty.nettyinpractice.reconnect;

import io.netty.channel.ChannelHandler;

/**
 * Created with IntelliJ IDEA.
 * Description: 用于获取handlers
 *
 * @author dongwei
 * @date 2018/04/12
 * Time: 15:51
 */
public interface ChannelHandlerHolder {
    ChannelHandler[] addHandlers();
}
