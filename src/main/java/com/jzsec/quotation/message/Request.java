package com.jzsec.quotation.message;


public class Request implements Message{

	private int msgTpye;
	private MessageBody body;

	public Request() {
	}

	public int getMsgTpye() {
		return msgTpye;
	}

	public void setMsgTpye(int msgTpye) {
		this.msgTpye = msgTpye;
	}

	public MessageBody getBody() {
		return body;
	}

	public void setBody(MessageBody body) {
		this.body = body;
	}
}
