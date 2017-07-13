package com.jzsec.quotation.proxy;

import com.jzsec.quotation.core.client.Client;
import com.jzsec.quotation.core.client.SimpleNettyClient;
import com.jzsec.quotation.transport.MessagePactTransport;
import com.jzsec.quotation.transport.Transport;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleProxyFactory implements ProxyFactroy {

	private String address = null;
	private ReentrantLock lock = new ReentrantLock(true);
	private Client client;
	private Transport transport;

	public SimpleProxyFactory(MessagePactTransport transport) {
		this.transport = transport;
	}

	public void setEnpoint(String address) {
		this.address = address;
	}

	public void dispose() throws IOException {
		client.close();
	}

	public Client getClient(String endpoint) throws IOException {
		this.client = new SimpleNettyClient(endpoint, transport);
		return this.client;
	}
}
