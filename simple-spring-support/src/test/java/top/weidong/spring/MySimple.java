package top.weidong.spring;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/07/27
 * Time: 15:01
 */
public class MySimple {

    private SimpleDateFormat format;

    private boolean f;

    public String print(){
        System.out.println("~~~~~~~~~~~~~~~~~");
        return format.format(new Date());
    }

    public boolean flag(){
        return this.f;
    }

    public SimpleDateFormat getFormat() {
        return format;
    }

    public void setFormat(SimpleDateFormat format) {
        this.format = format;
    }

    public void setFlag(boolean flag){
        this.f = flag;
    }
}
