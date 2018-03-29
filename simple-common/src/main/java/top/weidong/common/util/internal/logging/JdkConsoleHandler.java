package top.weidong.common.util.internal.logging;

import java.util.logging.ConsoleHandler;

/**
 * Created with IntelliJ IDEA.
 * Description: 将jdk日志输出改为正常的（不红的）
 *
 * @author dongwei
 * @date 2018/03/29
 * Time: 16:38
 */
public class JdkConsoleHandler extends ConsoleHandler{

    public JdkConsoleHandler(){
        super();
        setOutputStream(System.out);
    }
}
