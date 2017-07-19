package com.jzsec.quotation.transport;

import com.jzsec.quotation.codec.MessagePackDecoder;
import com.jzsec.quotation.codec.MessagePackDecoder1;
import com.jzsec.quotation.codec.MessagePackEncoder;
import io.netty.channel.ChannelHandlerAdapter;

public class MessagePactTransport implements Transport {

	public MessagePactTransport() {
	}

	public ChannelHandlerAdapter getEncoder() {
		return new MessagePackEncoder();
	}

	public ChannelHandlerAdapter getDecoder() {
//		return new MessagePackDecoder1(65535, 4, 4, 4);
		return new MessagePackDecoder();
	}

}
