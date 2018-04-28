package top.weidong.transport.channel;

import com.google.common.collect.Lists;
import top.weidong.common.util.internal.logging.InternalLogger;
import top.weidong.common.util.internal.logging.InternalLoggerFactory;
import top.weidong.transport.loop.BaseLoop;
import top.weidong.transport.loop.BossLoop;
import top.weidong.transport.loop.WorkerLoop;

import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author dongwei
 * @date 2018/04/28
 * Time: 12:41
 */
public class ServerAcceptorChannel {

    private static final InternalLogger LOGGER =
            InternalLoggerFactory.getInstance(ServerAcceptorChannel.class);

    final ServerSocket socket;
    private static final int SO_TIMEOUT = 500;


    private static ServerSocket newServerSocket() {
        try {
            return new ServerSocket();
        } catch (IOException e) {
            throw new RuntimeException("failed to create a server socket", e);
        }
    }


    public ServerAcceptorChannel() {
        this(newServerSocket());
    }


    public ServerAcceptorChannel(ServerSocket socket) {
        if (socket == null) {
            throw new NullPointerException("socket");
        }
        boolean success = false;
        try {
            socket.setSoTimeout(SO_TIMEOUT);
            success = true;
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to set the server socket timeout.", e);
        } finally {
            if (!success) {
                try {
                    socket.close();
                } catch (IOException e) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn(
                                "Failed to close a partially initialized socket.", e);
                    }
                }
            }
        }
        this.socket = socket;
    }

    public void bind(int port) throws Exception {
        doBind(new InetSocketAddress(port));
    }

    protected void doBind(SocketAddress localAddress) throws Exception {
        socket.bind(localAddress, 128);
    }

    public void startWithLoop(BossLoop boss, final WorkerLoop worker) throws Exception {
        final List<Socket> buf = Lists.newArrayList();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        int ret = processAcceptance(buf);
                        if (ret > 0) {
                            worker.processIO(buf);
                            buf.clear();
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
        };
        boss.doWork(task);
    }


    /**
     * 处理连接
     *
     * @param buf
     * @return
     * @throws Exception
     */
    protected int processAcceptance(final List<Socket> buf) throws Exception {
        if (socket.isClosed()) {
            return -1;
        }
        try {
            Socket s = socket.accept();
            try {
                buf.add(s);
                return 1;
            } catch (Throwable t) {
                LOGGER.warn("Failed to create a new channel from an accepted socket.", t);
                try {
                    s.close();
                } catch (Throwable t2) {
                    LOGGER.warn("Failed to close a socket.", t2);
                }
            }
        } catch (SocketTimeoutException e) {
            // Expected
        }
        return 0;
    }
}
