package com.jzsec.quotation.transport;

import com.jzsec.quotation.codec.MessagePackDecoder;
import com.jzsec.quotation.codec.MessagePackEncoder;
import io.netty.channel.ChannelHandlerAdapter;

public class MessagePactTransport implements Transport {

	public MessagePactTransport() {
	}

	public ChannelHandlerAdapter getEncoder() {
		return new MessagePackEncoder();
	}

	public ChannelHandlerAdapter getDecoder() {
		return new MessagePackDecoder(65535, 4, 4);
	}

}
