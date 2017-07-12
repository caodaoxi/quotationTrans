package com.jzsec.quotation.core.client;


import com.jzsec.quotation.handler.NettyClientHandler;

import java.io.IOException;

public abstract class NettyClient<P, T> implements Client {
	protected NettyClientHandler handler;
}
