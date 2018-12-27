package client;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: zhangbaoning
 * @date: 2018/12/26
 * @since: JDK 1.8
 * @description: TODO
 */
public class FixedLengthDecoder extends CumulativeProtocolDecoder {
    private final String charset;
    /**
     * 报文体长度
     */
    private final int dataLengthFieldValueLength;
    private final boolean isDataLengthFieldValueContainSelf;
    //报文长度字段从第几位开始
    private final int dataLengthFieldStartIndex;


    //注意这里使用了Mina自带的AttributeKey类来定义保存在IoSession中对象的键值,其可有效防止键值重复
    //通过查询AttributeKey类源码发现,它的构造方法采用的是"类名+键名+AttributeKey的hashCode"的方式


    private final AttributeKey CONTEXT = new AttributeKey(getClass(), "context");

    public FixedLengthDecoder(String charset, int dataLengthFieldValueLength, boolean isDataLengthFieldValueContainSelf, int dataLengthFieldStartIndex){
        this.charset = charset;
        this.dataLengthFieldValueLength = dataLengthFieldValueLength;
        this.isDataLengthFieldValueContainSelf = isDataLengthFieldValueContainSelf;
        this.dataLengthFieldStartIndex = dataLengthFieldStartIndex;
    }
    private Context getContext(IoSession session){
        Context context = (Context)session.getAttribute(CONTEXT);
        if(null == context){
            context = new Context();
            session.setAttribute(CONTEXT, context);
        }
        return context;
    }
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        Context ctx = this.getContext(session);
        IoBuffer buffer = ctx.innerBuffer;
        int messageCount = ctx.getMessageCount();

        //报文长度字段以及其前面的部分 总共的长度
        int dataLengthFieldAndBeforeLength = dataLengthFieldStartIndex + dataLengthFieldValueLength;
        //判断position和limit之间是否有元素
        while(in.hasRemaining()){
            //get()读取buffer的position的字节,然后position+1
            buffer.put(in.get());
            //约定:报文的前dataLengthFieldValueLength个字符表示报文总长度,不足dataLengthFieldValueLength位则左侧补0
            if(messageCount++ == (dataLengthFieldAndBeforeLength-1)){
                buffer.flip();       //Set limit=position and position=0 and mark=-1


                byte[] dataLengthFieldValueLengthByte = new byte[dataLengthFieldAndBeforeLength];
                buffer.get(dataLengthFieldValueLengthByte);
                String dataLengthFieldValueLengthString = new String(dataLengthFieldValueLengthByte, charset);
                String messageLengthString = dataLengthFieldValueLengthString.substring(dataLengthFieldStartIndex);

                try{
                    //请求报文有误时,Server可能返回非约定报文,此时会抛java.lang.NumberFormatException
                    ctx.setMessageLength(Integer.parseInt(messageLengthString.trim()));
                }catch(NumberFormatException e){

                    try {
                        byte[] dataLength = new byte[dataLengthFieldValueLengthByte.length-dataLengthFieldStartIndex];
                        System.arraycopy(dataLengthFieldValueLengthByte, dataLengthFieldStartIndex, dataLength, 0, dataLength.length);
                        int dataLengthInt = dataLength.length;
                        ctx.setMessageLength(dataLengthInt);
                    } catch (Exception e2) {
                        ctx.setMessageLength(in.limit());
                    }
                }
                //让两个IoBuffer的limit相等
                buffer.limit(in.limit());
            }
        }
        ctx.setMessageCount(messageCount);

        int messageTotalLength;//报文总体长度
        //报文长度字段表示总体包长
        if(isDataLengthFieldValueContainSelf){
            messageTotalLength = ctx.getMessageLength();
            //报文总长度小于报文长度字段长
            if(messageTotalLength<dataLengthFieldValueLength){
                //（待）错误处理*
            }
        }else {
            //报文长度字段表示 总体包长-报文长度字段长
            messageTotalLength = ctx.getMessageLength() + dataLengthFieldValueLength + dataLengthFieldStartIndex;
        }
        //傳過來的數據長度可能大於包長
        if(messageTotalLength <= buffer.position()){
            buffer.flip();
            byte[] message = new byte[messageTotalLength];
            buffer.get(message);
            // 编码后进行输出
            out.write(new String(message, charset));
            ctx.reset();
            return true;
        }else{
            return false;
        }

    }
    private class Context{
        /**
         * 用于累积数据的IoBuffer
         */
        private final IoBuffer innerBuffer;
        /**
         * 记录已读取的报文字节数
         */
        private int messageCount;
        /**
         * 记录已读取的报文头标识的报文长度
         */
        private int messageLength;
        public Context(){
            innerBuffer = IoBuffer.allocate(100).setAutoExpand(true);
        }
        public int getMessageCount() {
            return messageCount;
        }
        public void setMessageCount(int messageCount) {
            this.messageCount = messageCount;
        }
        public int getMessageLength() {
            return messageLength;
        }
        public void setMessageLength(int messageLength) {
            this.messageLength = messageLength;
        }
        public void reset(){
            //Set limit=capacity and position=0 and mark=-1
            this.innerBuffer.clear();
            this.messageCount = 0;
            this.messageLength = 0;
        }
    }  }

