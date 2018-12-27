package client;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

/**
 * @author: zhangbaoning
 * @date: 2018/12/26
 * @since: JDK 1.8
 * @description: TODO
 */
public class FixedLengthEncoder extends ProtocolEncoderAdapter {
    private final String charset;
    public FixedLengthEncoder(String charset){
        this.charset = charset;
    }
    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
//        通过使用一个auto-expanding（动态扩展的）缓冲对象调用
        IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
        //二者等效--><code>buffer.put(message.toString().getBytes(charset))</code>
        buffer.putString(message.toString(), Charset.forName(charset).newEncoder());
        buffer.flip();
        out.write(buffer);
    }
}
