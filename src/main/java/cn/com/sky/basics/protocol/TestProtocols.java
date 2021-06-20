package cn.com.sky.basics.protocol;

import io.netty.handler.codec.*;

/**
 *
 */
public class TestProtocols {

    public static void main(String[] args) {
//        Netty 常用编码器类型：

//        对象编码成字节流；
        MessageToByteEncoder messageToByteEncoder;

//        一种消息类型编码成另外一种消息类型。
        MessageToMessageEncoder messageToMessageEncoder;


//        Netty 常用解码器类型：
//        将字节流解码为消息对象；
        ByteToMessageDecoder byteToMessageDecoder;
        ReplayingDecoder replayingDecoder;


//        将一种消息类型解码为另外一种消息类型。
        MessageToMessageDecoder messageToMessageDecoder;


//        固定长度解码器
        FixedLengthFrameDecoder fixedLengthFrameDecoder;

//        特殊分隔符解码器
        DelimiterBasedFrameDecoder delimiterBasedFrameDecoder;


//        长度域解码器
        LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder;


    }
}