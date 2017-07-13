package com.jzsec.quotation.core.client;

import com.jzsec.quotation.handler.SimpleNettyClientHandler;
import com.jzsec.quotation.message.Request;
import com.jzsec.quotation.message.Response;
import com.jzsec.quotation.transport.Transport;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleNettyClient extends NettyClient<Request, Response>  {

	private Bootstrap bootstrap;
	private Channel session = null;
	private String serverAddress;
	private AtomicInteger index = new AtomicInteger(0);
	private AtomicBoolean state = new AtomicBoolean(false);
	private Transport transport;
	public SimpleNettyClient(String address, Transport transport) {
		EventLoopGroup work = new NioEventLoopGroup(10);
		this.transport = transport;
		handler = new SimpleNettyClientHandler();
		bootstrap = new Bootstrap();
		bootstrap.group(work);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
		bootstrap.option(ChannelOption.SO_RCVBUF, 65535);
		bootstrap.option(ChannelOption.SO_SNDBUF, 65535);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline()
						.addLast(SimpleNettyClient.this.transport.getDecoder(),
								 SimpleNettyClient.this.transport.getEncoder(),
								(SimpleNettyClientHandler) handler);
			}
		});
		this.serverAddress = address;
	}

	public void close() throws IOException {
		if (state.compareAndSet(true, false)) {
			session.close();
			session = null;
		}
	}

	public void connect() throws IOException {
		String[] host = this.serverAddress.split(":");
		ChannelFuture future = null;
		try {
			future = bootstrap.connect(host[0], Integer.valueOf(host[1])).sync();
			this.session = future.channel();
//			this.session.closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (session == null) {
			state.set(false);
			throw new IOException("can not connect to server ...!");
		}
	}

	public void login() throws IOException {
		Request request = new Request();
		request.setMsgTpye(1);
		request.setSourceId("VSS4");
		request.setTargetId("YL");
		request.setCreateTime(System.currentTimeMillis());
		request.setHeartbeat(100);
		request.setVersion("1.01");
		request.setPassword("");
		this.getSession().writeAndFlush(request);
		try {
			this.session.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	private Channel getSession() {
		return session;
	}

	public boolean isActive() {
		return state.get();
	}

}
