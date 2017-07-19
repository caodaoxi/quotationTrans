package com.jzsec.quotation.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessagePackDecoder2 extends ByteToMessageDecoder {
	private static byte[] waitSend = new byte[0];

	public MessagePackDecoder2() {
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

				bodyLength = byteArray2Int(Arrays.copyOfRange(msg, 4 + index, 8 + index));// 解析出包体长度
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
		Integer msgType = byte2Int(Arrays.copyOfRange(msgBody, 0, 4));
//		Response response = new Response();
//		response.setMsgType(msgType);
//		response.setBody();
		if (msgType == 300111 || msgType == 309011) {// 判断是否为快照行情
			deCodePblic(msgBody);
		}
	}


	public static long byte2Long(byte[] bs) {
		assert bs.length >= 8;
		ByteBuffer buf = ByteBuffer.allocate(bs.length);
		buf.put(bs);
		buf.flip();
		long result = buf.getLong();
		return result;

	}

	public static int byte2Int(byte[] b) {
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

	/**
	 * @return int
	 */
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


	public void deCodePblic(byte[] msg) {
		String OrigTime = byte2Long(Arrays.copyOfRange(msg, 8, 16)) + "";// 数据生成时间
		String MDStreamID = new String(Arrays.copyOfRange(msg, 18, 21));// 行情类别
		String SecurityID = new String(Arrays.copyOfRange(msg, 21, 29));// 股票代码
		System.out.println(OrigTime + " : " + MDStreamID + " : " + SecurityID);
	}
}
