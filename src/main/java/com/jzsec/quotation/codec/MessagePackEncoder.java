package com.jzsec.quotation.codec;

import com.jzsec.quotation.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;
import org.msgpack.packer.MessagePackBufferPacker;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class MessagePackEncoder extends MessageToByteEncoder<Message> {
	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out)
			throws Exception {
		if (msg instanceof Request) {
			Request req = (Request) msg;
			MessageBody body = req.getBody();

			byte[] bytes = body.encode();
			int checkSum = getCheckSum(req, bytes);
			byte[] requestBytes = encodeRequest(req, bytes, checkSum);
//			if(body instanceof Hearbeat) {
//				System.out.println("heabeat : " + Arrays.toString(requestBytes));
//			}
			out.writeBytes(requestBytes);
		}
	}

	public int getCheckSum(Request req, byte[] bytes) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeInt(req.getMsgTpye());
			dos.writeInt(bytes.length);
			if (bytes.length > 0) dos.write(bytes, 0, bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dos.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		byte[] data = bos.toByteArray();

		int idx, cks;
		for (idx = 0, cks = 0; idx < data.length; cks += (int) (data[idx++] & 0xff))
			;
		return cks % 256;
	}

	public byte[] encodeRequest(Request req, byte[] bytes, int checkSum) {

		ByteBuffer bb = ByteBuffer.allocate(8 + bytes.length + 4);

		bb.putInt(req.getMsgTpye());
		bb.putInt(bytes.length);
		if (bytes.length > 0) bb.put(bytes, 0, bytes.length);
		bb.putInt(checkSum);
		bb.flip();
		return bb.array();
	}
}
