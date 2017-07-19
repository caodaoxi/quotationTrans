package com.jzsec.quotation.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

public class MyLengthFieldBasedFrameDecoder extends ByteToMessageDecoder {

    private static byte[] waitSend = new byte[0];


    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        this.decode(ctx, in, out);
    }

    protected void decode(ByteBuf in, List<Object> out) throws Exception {
        int length = in.readableBytes();

        if(length > 0) {
            byte[] bytes = new byte[length];
            int index = 0;
            int bodyLength = 0;
            for (int i = 0; i < length; i++) {
                byte[] b = null;
                if (length >= 8 + index) {
                    bodyLength = byteArray2Int(Arrays.copyOfRange(bytes, 4 + index, 8 + index));
                    if (bytes.length >= 8 + bodyLength + 4 + index) {
                        b = Arrays.copyOfRange(bytes, index, index + 8 + bodyLength + 4);
                        out.add(b);
                    } else {
                        if (index == bytes.length) {
                            waitSend = new byte[0];
                        } else {
                            waitSend = Arrays.copyOfRange(bytes, index, bytes.length);
                        }
                        break;
                    }
                } else {
                    if (index == bytes.length) {
                        waitSend = new byte[0];
                    } else {
                        waitSend = Arrays.copyOfRange(bytes, index, bytes.length);
                    }
                    break;
                }
                index += bodyLength + 8 + 4;
            }
        }
    }


    public static int byteArray2Int(byte[] b) {
        byte[] a = new byte[4];
        int i = a.length - 1, j = b.length - 1;
        for (; i >= 0; i--, j--) {// 从b的尾部(即int值的低位)开始copy数据
            if (j >= 0)
                a[i] = b[j];
            else
                a[i] = 0;// 如果b.length不足4,则将高位补0
        }
        int v0 = (a[0] & 0xff) << 24;// &0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
        int v1 = (a[1] & 0xff) << 16;
        int v2 = (a[2] & 0xff) << 8;
        int v3 = (a[3] & 0xff);
        return v0 + v1 + v2 + v3;
    }
}
