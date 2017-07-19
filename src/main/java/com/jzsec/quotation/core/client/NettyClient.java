package com.jzsec.quotation.core.client;



import com.jzsec.quotation.handler.NettyClientHandler;

import java.util.concurrent.atomic.AtomicBoolean;


public abstract class NettyClient<P, T> implements Client {
	protected AtomicBoolean state = new AtomicBoolean(false);
	protected NettyClientHandler handler;
}
