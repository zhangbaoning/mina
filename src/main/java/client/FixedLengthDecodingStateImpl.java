package client;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.statemachine.DecodingState;
import org.apache.mina.filter.codec.statemachine.FixedLengthDecodingState;

/**
 * @author: zhangbaoning
 * @date: 2018/12/26
 * @since: JDK 1.8
 * @description: TODO
 */
public class FixedLengthDecodingStateImpl extends FixedLengthDecodingState {
    public String state = "";
    public final static String SUCCESS = "SUCCESS";
    public FixedLengthDecodingStateImpl(int length)
    {
        super(length);
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }
    @Override
    protected DecodingState finishDecode(IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        int i = ioBuffer.getInt();
        protocolDecoderOutput.write(i);
        this.state =FixedLengthDecodingStateImpl.SUCCESS;
        return this;
    }
}
