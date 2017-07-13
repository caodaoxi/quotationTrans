package com.jzsec.quotation.codec;

import com.jzsec.quotation.message.Message;
import com.jzsec.quotation.message.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;
import org.msgpack.packer.MessagePackBufferPacker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;


public class MessagePackEncoder extends MessageToByteEncoder<Message> {
	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out)
			throws Exception {
		MessagePackBufferPacker buffer = new MessagePackBufferPacker(new MessagePack());
		if (msg instanceof Request) {
			Request req = (Request) msg;
			byte[] bytes = encodeBody(req);
			int checkSum = getCheckSum(req, bytes);
			buffer.write(req.getMsgTpye());
			buffer.write(bytes.length);
			buffer.write(bytes);
			buffer.write(checkSum);
		} else {
			buffer.close();
			throw new Exception("not support message type :["+msg.getClass().getSimpleName()+"]");
		}
		out.writeBytes(buffer.toByteArray());
		buffer.close();
	}


	public byte[] encodeBody(Request req) {
		byte[] empty = new byte[64];
		Arrays.fill(empty, (byte)0x20);
		MessagePackBufferPacker buffer = new MessagePackBufferPacker(new MessagePack());
		byte[] data = null;
		try {
//			data = req.getSourceId().getBytes("UTF-8");
//			buffer.write(data);
//			buffer.write(empty, 0, 20 - data.length);

//			data = req.getTargetId().getBytes("UTF-8");
//			buffer.write(data);
//			buffer.write(empty, 0, 20 - data.length);

//			buffer.write(req.getHeartbeat());

//			data = req.getPassword().getBytes("UTF-8");
//			buffer.write(data);
//			buffer.write(empty, 0, 16 - data.length);

			data = req.getVersion().getBytes("UTF-8");
			buffer.write(data);
//			buffer.write(empty, 0, 32 - data.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	public int getCheckSum(Request req, byte[] bytes) {
		MessagePackBufferPacker buffer = new MessagePackBufferPacker(new MessagePack());
		try {
			buffer.write(req.getMsgTpye());
			buffer.write(bytes.length);
			buffer.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = buffer.toByteArray();
		int idx, cks;
		for (idx = 0, cks = 0; idx < data.length; cks += (int) (data[idx++] & 0xff));
		return cks % 256;
	}
}
