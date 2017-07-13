package com.jzsec.quotation.handler;

import com.jzsec.quotation.message.Message;
import com.jzsec.quotation.message.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;

public class SimpleNettyClientHandler extends SimpleChannelInboundHandler<Message> implements NettyClientHandler {

	public BlockingQueue<Response> queue;


	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception {
		if (msg instanceof Response) {
			Response response = (Response) msg;
			queue.add(response);
			System.out.println(1);
		}
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.channel().close();
	}


	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		//TODO notify proxyFacotry to remove the cache channel
		super.close(ctx, promise);
	}

}
