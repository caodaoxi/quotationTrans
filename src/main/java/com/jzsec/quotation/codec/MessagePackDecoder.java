package com.jzsec.quotation.codec;

import com.jzsec.quotation.message.Response;
import com.jzsec.quotation.util.BytesUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MessagePackDecoder extends ByteToMessageDecoder {
	private static byte[] waitSend = new byte[0];

	public MessagePackDecoder() {
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> responses) throws Exception {
		int len = byteBuf.readableBytes();
		byte[] buffer_part;
		if (len > 0) {
			buffer_part = new byte[len];
			if (buffer_part.length != 0) {
				byteBuf.readBytes(buffer_part);
			}
			byte[] send = concat(waitSend, buffer_part);
			List<byte[]> list = checkMsg(send);
			for (int i = 0; i < list.size(); i++) {
				parsig(list.get(i), responses);
			}
		}
	}

	private byte[] concat(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	public static List<byte[]> checkMsg(byte[] msg) {
		// System.out.println(msg.length);
		int bodyLength = 0;
		List<byte[]> list = new ArrayList<byte[]>();
		int index = 0;
		for (int i = 0; i < msg.length; i++) {
			byte[] b = null;
			if (msg.length >= 8 + index) {
				bodyLength = BytesUtil.byteArray2Int(Arrays.copyOfRange(msg, 4 + index, 8 + index));// 解析出包体长度
				if (msg.length >= 8 + bodyLength + 4 + index) {
					b = Arrays.copyOfRange(msg, index, index + 8 + bodyLength + 4);
					list.add(b);
				} else {
					if (index == msg.length) {
						waitSend = new byte[0];
					} else {
						waitSend = Arrays.copyOfRange(msg, index, msg.length);
					}
					break;
				}
			} else {
				if (index == msg.length) {
					waitSend = new byte[0];
				} else {
					waitSend = Arrays.copyOfRange(msg, index, msg.length);
				}
				break;
			}
			index += bodyLength + 8 + 4;
		}
		return list;
	}

	private void parsig(byte[] msg, List<Object> responses) {
		byte[] msgBody = msg;
		Integer msgType = BytesUtil.byte2Int(Arrays.copyOfRange(msgBody, 0, 4));
		Response response = new Response();
		response.setMsgType(msgType);
		response.setBody(msg);
		responses.add(response);
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
