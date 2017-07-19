package com.jzsec.quotation.codec;

import com.jzsec.quotation.message.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MessagePackDecoder1 extends BodyLengthAdjustFieldBasedFrameDecoder {

	public MessagePackDecoder1(ByteOrder byteOrder, int maxFrameLength,
							   int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
							   int initialBytesToStrip, long bodyLengthAdjustment, boolean failFast) {
		super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip, bodyLengthAdjustment, failFast);
	}

	public MessagePackDecoder1(int maxFrameLength, int lengthFieldOffset,
							   int lengthFieldLength, int lengthAdjustment,
							   int initialBytesToStrip, long bodyLengthAdjustment, boolean failFast) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip, bodyLengthAdjustment, failFast);
	}

	public MessagePackDecoder1(int maxFrameLength, int lengthFieldOffset,
							   int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, long bodyLengthAdjustment) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip,bodyLengthAdjustment );
	}

	public MessagePackDecoder1(int maxFrameLength, int lengthFieldOffset,
							   int lengthFieldLength, long bodyLengthAdjustment) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, bodyLengthAdjustment);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		Object frame = super.decode(ctx, in);
		Response response = new Response();
		if (frame != null && frame instanceof ByteBuf) {
			ByteBuf buf = (ByteBuf) frame;
			int msgType = buf.readInt();
			response.setMsgType(msgType);
			int size = buf.readInt();
			byte[] body = new byte[size];
			buf.readBytes(body);
			response.setBody(body);
//			int checkSum = buf.readInt();
//			response.setChecksum(checkSum);
			buf.release();
			if(300111 == msgType || 309011 == msgType) {
				decodePublic(body);
			}
			return response;
		}
		return null;
	}

	public void decodePublic(byte[] body) {
		String OrigTime = byte2Long(Arrays.copyOfRange(body, 8, 16)) + "";// 数据生成时间
		String MDStreamID = new String(Arrays.copyOfRange(body, 18, 21));// 行情类别
		String SecurityID = new String(Arrays.copyOfRange(body, 21, 29));// 股票代码
		String TradingPhaseCode = new String(Arrays.copyOfRange(body, 33, 41));// 产品所处
		System.out.println(OrigTime + " : " + MDStreamID + " : " + SecurityID);
	}

	public static long byte2Long(byte[] bs) {
		assert bs.length >= 8;
		ByteBuffer buf = ByteBuffer.allocate(bs.length);
		buf.put(bs);
		buf.flip();
		long result = buf.getLong();
		return result;

	}
}
