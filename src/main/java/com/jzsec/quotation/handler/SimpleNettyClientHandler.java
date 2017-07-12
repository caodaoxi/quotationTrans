package com.jzsec.quotation.handler;

import com.jzsec.quotation.message.Message;
import com.jzsec.quotation.message.Request;
import com.jzsec.quotation.message.RespFuture;
import com.jzsec.quotation.message.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;

public class SimpleNettyClientHandler extends
		SimpleChannelInboundHandler<Message> implements NettyClientHandler {

	private ConcurrentHashMap<Long, RespFuture> responseMap = new ConcurrentHashMap<Long, RespFuture>();

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception {
		if (msg instanceof Request) {

		} else if (msg instanceof Response) {
			Response resp = (Response) msg;
			RespFuture future = responseMap.get(resp.getReqId());
			if (future != null) {
				future.fillResponse(resp);
			} else {
				// TODO
			}
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


	public void putResponse(Long key, RespFuture futrue) {
		responseMap.put(key, futrue);
	}

	public void removeReponse(Long key) {
		responseMap.remove(key);
	}
}
