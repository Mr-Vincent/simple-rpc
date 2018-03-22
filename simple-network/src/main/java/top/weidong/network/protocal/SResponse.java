package top.weidong.network.protocal;

/**
 * Created with IntelliJ IDEA.
 * Description: response 消息封装
 *
 * @author dongwei
 * @date 2018/03/22
 * Time: 15:12
 */
public class SResponse {

    private String requestId;
    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "SResponse{" +
                "requestId='" + requestId + '\'' +
                ", error='" + error + '\'' +
                ", result=" + result +
                '}';
    }
}
