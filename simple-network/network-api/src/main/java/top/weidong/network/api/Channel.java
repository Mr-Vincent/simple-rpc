package top.weidong.network.api;

import java.io.IOException;

/**
 * @author dongwei
 * @since 2020/09/14
 * Time: 15:26
 * 对连接的抽象
 */
public interface Channel {



    /**
     * 获取channel的id
     * @return
     */
    String id();


    /**
     * 写数据
     * @param message
     * @return
     */
    Channel write(Payload message) throws IOException;

}
