package top.weidong.service.processor;

import too.weidong.network.bio.processor.Processor;

import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 10:43
 */
public abstract class AbstractProcessor implements Processor{

    @Override
    public void process(Socket client) {

    }
}
