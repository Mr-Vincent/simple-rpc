package top.weidong.registry;

import top.weidong.network.Directory;

/**
 * Created with IntelliJ IDEA.
 * Description: 注册信息
 *
 * @author dongwei
 * @date 2018/03/29
 * Time: 21:43
 */
public class RegisterMeta extends Directory {

    private String providerName;

    private String serverAddress;

    public RegisterMeta() {
    }

    public RegisterMeta(String providerName, String serverAddress) {
        this.providerName = providerName;
        this.serverAddress = serverAddress;
    }

    public RegisterMeta(String providerName) {
        this(providerName,null);
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    /**
     * 转为对象
     * @param clazz
     * @return
     */
    public static RegisterMeta fromClazz(Class<?> clazz){
        return new RegisterMeta(clazz.getName());
    }

    /**
     * 转为对象
     * @param clazz
     * @param address
     * @return
     */
    public static RegisterMeta fromClazz(Class<?> clazz,String address){
        return new RegisterMeta(clazz.getName(),address);
    }

    @Override
    public String getServiceProviderName() {
        return providerName;
    }

    @Override
    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public String toString() {
        return "RegisterMeta{" +
                "providerName='" + providerName + '\'' +
                ", serverAddress='" + serverAddress + '\'' +
                '}';
    }
}
