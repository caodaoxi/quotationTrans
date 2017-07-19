package com.jzsec.quotation.handler;

import com.jzsec.quotation.core.client.KafkaClient;
import com.jzsec.quotation.core.client.NettyClient;
import com.jzsec.quotation.message.Message;
import com.jzsec.quotation.message.Response;
import com.jzsec.quotation.queue.MsgQueueManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleNettyClientHandler extends SimpleChannelInboundHandler<Message> implements NettyClientHandler {

	public BlockingQueue<Response> queue;
	private NettyClient client = null;
	private MsgQueueManager msgQueue = new MsgQueueManager();
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	public SimpleNettyClientHandler(XMLConfiguration config) {
	}

	public SimpleNettyClientHandler(NettyClient client, XMLConfiguration config) {
		this.client = client;
		KafkaClient kafkaClient = new KafkaClient(config);
		for(int i = 0; i < 10; i++) {
			threadPool.submit(new ParserConsumer(msgQueue, kafkaClient));
		}

	}


	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception {
		if (msg instanceof Response) {
			Response response = (Response) msg;
			if(response.getMsgType() == 1) {
				client.setActive(true);
			} else {
//				System.out.println(response.getMsgType());
				msgQueue.put(response);
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

}
