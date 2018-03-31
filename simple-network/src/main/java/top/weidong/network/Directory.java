package top.weidong.network;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/31
 * Time: 10:45
 */
public abstract class Directory {
    /**
     * 获取服务名
     * @return
     */
    public abstract String getServiceProviderName();

    /**
     * 获取服务地址
     * @return
     */
    public abstract String getServerAddress();
}
