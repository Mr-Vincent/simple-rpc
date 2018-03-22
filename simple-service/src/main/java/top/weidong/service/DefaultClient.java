package top.weidong.service;

import top.weidong.network.SClient;

import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 11:41
 */
public class DefaultClient {

    private SClient client;

    /**
     * 添加客户端
     * @param client
     * @return
     */
    public DefaultClient withClient(SClient client){
        this.client = client;
        return this;
    }

    public Socket getSocket(){
        return client.getSocket();
    }

}
