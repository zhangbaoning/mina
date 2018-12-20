package client;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * @author: zhangbaoning
 * @date: 2018/12/19
 * @since: JDK 1.8
 * @description: 连接工具
 */
public class ConnectUtils {
    public static void main(String[] args) throws Throwable {
        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(2000);
        final String HOSTNAME = ReadPropUtils.getByKey("hostname");
        final String PORT = ReadPropUtils.getByKey("port");
       /* if (USE_CUSTOM_CODEC) {
            connector.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new SumUpProtocolCodecFactory(false)));
        } else {
            connector.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        }

        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.setHandler(new ClientSessionHandler(values));*/
        IoSession session;

        for (;;) {
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, Integer.parseInt(PORT)));
                future.awaitUninterruptibly();
                session = future.getSession();
                break;
            } catch (RuntimeIoException e) {
                System.err.println("Failed to connect.&quot;");
                e.printStackTrace();
                Thread.sleep(5000);
            }
        }

        // wait until the summation is done
        session.getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }
}
