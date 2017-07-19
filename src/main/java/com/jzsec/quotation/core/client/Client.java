package com.jzsec.quotation.core.client;

import com.jzsec.quotation.message.Request;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface Client {

	public void close() throws IOException;

	public void connect() throws IOException;

	public void login(long timeout) throws IOException, TimeoutException, InterruptedException ;
	
	public boolean isActive();

	public void setActive(boolean isActive);
}
