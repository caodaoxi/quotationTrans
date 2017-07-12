package com.jzsec.quotation.transport;

import io.netty.channel.ChannelHandlerAdapter;

public interface Transport {

	public ChannelHandlerAdapter getEncoder();

	public ChannelHandlerAdapter getDecoder();
}
