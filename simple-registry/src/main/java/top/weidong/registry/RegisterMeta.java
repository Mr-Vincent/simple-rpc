package top.weidong.registry;

/**
 * Created with IntelliJ IDEA.
 * Description: 注册信息
 *
 * @author dongwei
 * @date 2018/03/29
 * Time: 21:43
 */
public class RegisterMeta {

    private String providerName;


    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    @Override
    public String toString() {
        return "RegisterMeta{" +
                "providerName='" + providerName + '\'' +
                '}';
    }
}
