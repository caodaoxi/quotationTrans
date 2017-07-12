package com.jzsec.quotation.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.msgpack.MessagePack;
import org.msgpack.unpacker.MessagePackBufferUnpacker;

import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

public class MessagePackDecoder extends LengthFieldBasedFrameDecoder {

	public MessagePackDecoder(ByteOrder byteOrder, int maxFrameLength,
			int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip, boolean failFast) {
		super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip, failFast);
	}

	public MessagePackDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip, boolean failFast) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip, failFast);
	}

	public MessagePackDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
				lengthAdjustment, initialBytesToStrip);
	}

	public MessagePackDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		Object frame = super.decode(ctx, in);
		if (frame != null && frame instanceof ByteBuf) {
			ByteBuf buf = (ByteBuf) frame;
			int size = buf.readInt();
			byte[] context = new byte[size];
			buf.readBytes(context);
			MessagePackBufferUnpacker unpacker = new MessagePackBufferUnpacker(
					new MessagePack(), size);
			unpacker.feed(context);
			Message message = warpMessage(unpacker);
			unpacker.close();
			buf.release();
			return message;
		}
		return null;
	}

	private Message warpMessage(MessagePackBufferUnpacker unpacker)
			throws Exception {
		int msgType = unpacker.readInt();
		if (0 == msgType) {
			Request req = new Request(unpacker.readLong());
			req.setClassName(unpacker.readString());
			req.setMethodName(unpacker.readString());
			req.setParamTypes(unpacker.readString());
			if (null != req.getParamTypes()
					&& !"".equals(req.getParamTypes().trim())) {
				List<Object> params = new LinkedList<Object>();
				for (String type : req.getParamTypes().split(",")) {
					params.add(unpacker.read(Class.forName(type)));
				}
				req.setParamValues(params);
			}
			return req;
		} else {
			Response resp = new Response();
			resp.setReqId(unpacker.readLong());
			resp.setRespCode(unpacker.readInt());
			resp.setParamType(unpacker.readString());
			resp.setError(unpacker.readString());
			if (null != resp.getParamType()
					&& !"".equals(resp.getParamType().trim())) {
				resp.setResponseEntry(unpacker.read(Class.forName(resp
						.getParamType())));
			}
			return resp;
		}
	}

}
