package client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * @author: zhangbaoning
 * @date: 2018/12/26
 * @since: JDK 1.8
 * @description: TODO
 */
public class FixedLengthIoHandlerAdapter extends IoHandlerAdapter {
    private String message;
    public FixedLengthIoHandlerAdapter(String message){
        this.message = message;
    }
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.write(message);
        System.out.println("write"+message);
    }
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        session.close(false);
        session.getService().dispose(false);
        System.out.println("rece"+message);

    }
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        session.close(true);
        session.getService().dispose(false);
    }
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        session.close(true);
        session.getService().dispose(false);
    }
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        session.close(true);
        session.getService().dispose(false);
    }
}
