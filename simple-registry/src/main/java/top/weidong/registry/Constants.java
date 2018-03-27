package top.weidong.registry;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/03/27
 * Time: 21:10
 */
public interface Constants {
    int ZK_SESSION_TIMEOUT = 5000;
    String ZK_REGISTRY_PATH = "/provider";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}
