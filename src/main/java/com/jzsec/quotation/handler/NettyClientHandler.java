package com.jzsec.quotation.handler;


import com.jzsec.quotation.message.RespFuture;

public interface NettyClientHandler {
	public void putResponse(Long key, RespFuture futrue);
	
	public void removeReponse(Long key);
}
