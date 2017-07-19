package com.jzsec.quotation.core.client;

import com.jzsec.quotation.handler.SimpleNettyClientHandler;
import com.jzsec.quotation.message.Hearbeat;
import com.jzsec.quotation.message.LoginBody;
import com.jzsec.quotation.message.Request;
import com.jzsec.quotation.message.Response;
import com.jzsec.quotation.transport.Transport;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleNettyClient extends NettyClient<Request, Response>  {

	private Bootstrap bootstrap;
	private Channel session = null;
	private String serverAddress;
	private AtomicInteger index = new AtomicInteger(0);
	private Transport transport;
	private CountDownLatch latch = new CountDownLatch(1);
	private XMLConfiguration config = null;

	public SimpleNettyClient(Transport transport, XMLConfiguration config) {
		EventLoopGroup work = new NioEventLoopGroup(10);
		this.transport = transport;
		this.config = config;
		this.serverAddress = config.getString("tsConfig.socketAddress") + ":" + config.getString("tsConfig.socketPort");
		handler = new SimpleNettyClientHandler(this, config);
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

	public void login(long timeout) throws IOException, TimeoutException, InterruptedException {
		Request request = new Request();
		LoginBody loginBody = new LoginBody();
		request.setMsgTpye(1);
		loginBody.setSourceId(config.getString("tsConfig.senderCompID"));
		loginBody.setTargetId(config.getString("tsConfig.targetCompID"));
		loginBody.setCreateTime(System.currentTimeMillis());
		loginBody.setHeartbeat(config.getInt("tsConfig.hearbeat"));
		loginBody.setVersion(config.getString("tsConfig.defaultAppVerID"));
		loginBody.setPassword(config.getString("tsConfig.password"));
		request.setBody(loginBody);
		this.getSession().writeAndFlush(request);

		if (-1 == timeout) {
			this.latch.await();
		} else {
			this.latch.await(timeout, TimeUnit.SECONDS);
		}
		if (this.isActive() == false) {
			throw new TimeoutException("Login time out");
		} else {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						SimpleNettyClient.this.sendHearbeat();
						try {
							Thread.sleep(config.getInt("tsConfig.hearbeat")/2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

	private Channel getSession() {
		return session;
	}

	public boolean isActive() {
		return state.get();
	}

	public void setActive(boolean isActive) {
		state.set(isActive);
	}

	public void sendHearbeat() {
		Request request = new Request();
		request.setMsgTpye(3);
		Hearbeat hearbeat = new Hearbeat();
		request.setBody(hearbeat);
		this.getSession().writeAndFlush(request);
	}
}
