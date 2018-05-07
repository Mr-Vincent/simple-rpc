package top.weidong.transport.handler;

import top.weidong.transport.channel.HandlerContext;

import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/28
 * Time: 17:43
 */
public interface Handler {


    void process(Socket socket);

    void channelActive(HandlerContext ctx) throws Exception;

    void channelInActive(HandlerContext ctx) throws Exception;
}
