package com.jzsec.quotation.core.client;

import com.jzsec.quotation.message.Request;

import java.io.IOException;

public interface Client {

	public void close() throws IOException;

	public void connect() throws IOException;

	public void login() throws IOException;
	
	public boolean isActive();

}
