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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleNettyClient extends NettyClient<Request, Response> {

	private Bootstrap bootstrap;
	private List<Channel> sessions = new LinkedList<Channel>();
	private List<String> serverAddress;
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
		this.serverAddress = Arrays.asList(address.split(","));
	}

	public void close() throws IOException {
		if (state.compareAndSet(true, false)) {
			for (Channel c : sessions) {
				c.close();
			}
			sessions.clear();
		}
	}

	public void connect() throws IOException {
		if (state.compareAndSet(false, true)) {
			for (String address : serverAddress) {
				String[] host = address.split(":");
				try {
					ChannelFuture future = bootstrap.connect(host[0],
							Integer.valueOf(host[1])).sync();
					sessions.add(future.channel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (sessions.size() == 0) {
				state.set(false);
				throw new IOException("can not connect to server ...!");
			}
		}
	}

	public boolean login(Request p) throws IOException {
		select().writeAndFlush(p);
		return true;
	}


	private Channel select() {
		index.compareAndSet(sessions.size(), 0);
		return sessions.get(index.getAndIncrement());
	}

	public boolean isActive() {
		return state.get();
	}

}
