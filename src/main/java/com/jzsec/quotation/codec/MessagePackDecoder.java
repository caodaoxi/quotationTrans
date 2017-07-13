package com.jzsec.quotation.codec;

import com.jzsec.quotation.message.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.ByteOrder;

public class MessagePackDecoder extends BodyLengthAdjustFieldBasedFrameDecoder {

	public MessagePackDecoder(ByteOrder byteOrder, int maxFrameLength,
			int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip, long bodyLengthAdjustment, boolean failFast) {
		super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip, bodyLengthAdjustment, failFast);
	}

	public MessagePackDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip, long bodyLengthAdjustment, boolean failFast) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip, bodyLengthAdjustment, failFast);
	}

	public MessagePackDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, long bodyLengthAdjustment) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip,bodyLengthAdjustment );
	}

	public MessagePackDecoder(int maxFrameLength, int lengthFieldOffset,
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
			int checkSum = buf.readInt();
			response.setChecksum(checkSum);
			buf.release();
			return response;
		}
		return null;
	}

}
