package top.weidong.transport.handler;

import top.weidong.common.util.internal.logging.InternalLogLevel;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.common.util.internal.logging.LogLevel;
import top.weidong.transport.channel.HandlerContext;

import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/28
 * Time: 17:54
 */
public class LoggerHandler implements Handler {

    private final LogLevel level;

    protected final InternalLogger logger;

    protected final InternalLogLevel internalLevel;

    private static final LogLevel DEFAULT_LEVEL = LogLevel.DEBUG;


    public LoggerHandler() {
        this(DEFAULT_LEVEL);
    }

    public LoggerHandler(LogLevel level) {
        if (level == null) {
            throw new NullPointerException("level");
        }

        logger = InternalLoggerFactory.getInstance(getClass());
        this.level = level;
        internalLevel = level.toInternalLevel();
    }

    @Override
    public void process(Socket socket) {
        if (socket.isConnected()) {
            logger.debug("ACTIVE");
        }
    }

    @Override
    public void channelActive(HandlerContext ctx) throws Exception {
        if (logger.isEnabled(internalLevel)) {
            logger.log(internalLevel, format(ctx, "ACTIVE"));
        }
        ctx.fireChannelActive();
    }

    @Override
    public void channelInActive(HandlerContext ctx) throws Exception {
        if (logger.isEnabled(internalLevel)) {
            logger.log(internalLevel, format(ctx, "INACTIVE"));
        }
        ctx.fireChannelInactive();
    }

    /**
     * Formats an event and returns the formatted message.
     *
     * @param eventName the name of the event
     */
    protected String format(HandlerContext ctx, String eventName) {
        String chStr = ctx.channel().toString();
        return new StringBuilder(chStr.length() + 1 + eventName.length())
                .append(chStr)
                .append(' ')
                .append(eventName)
                .toString();
    }
}
