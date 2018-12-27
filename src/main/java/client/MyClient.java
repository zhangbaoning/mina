package client;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhangbaoning
 * @date: 2018/12/24
 * @since: JDK 1.8
 * @description: TODO
 */
public class MyClient {
    public static void main(String[] args) {
        NioSocketConnector connector = new NioSocketConnector();

        connector.setConnectTimeoutMillis(30000L);

        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new FixedLengthEncoder("GBK"), new FixedLengthDecoder("GBK",6,true,6)));
        connector.setHandler(new FixedLengthIoHandlerAdapter("14"));

        InetSocketAddress address = new InetSocketAddress("192.168.2.11",8111);
        IoSession session = connector.connect(address).awaitUninterruptibly().getSession();

        //现在已实现了连接，接下来就是发送-接收-断开了

        try {

            // 发送

//            session.write(14);
            session.getCloseFuture().awaitUninterruptibly();

            // 接收
            session.getConfig().setUseReadOperation(true); //设置IoSession的read()方法为可用,默认为false
            ReadFuture readFuture = session.read();
            readFuture.awaitUninterruptibly();
            Object msg = readFuture.getMessage();
            System.out.println(msg);


        } finally {

            // 断开

            session.close(true);

            session.getService().dispose();

        }

    }
}
