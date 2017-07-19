package com.jzsec.quotation.proxy;

import com.jzsec.quotation.core.client.Client;
import com.jzsec.quotation.core.client.SimpleNettyClient;
import com.jzsec.quotation.transport.MessagePactTransport;
import com.jzsec.quotation.transport.Transport;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleProxyFactory implements ProxyFactroy {

	private ReentrantLock lock = new ReentrantLock(true);
	private Client client;
	private Transport transport;
	private XMLConfiguration config = null;

	public SimpleProxyFactory(MessagePactTransport transport, XMLConfiguration config) {
		this.transport = transport;
		this.config = config;
	}

	public void setEnpoint(String address) {
	}

	public void dispose() throws IOException {
		client.close();
	}

	public Client getClient() throws IOException {
		this.client = new SimpleNettyClient(transport, config);
		return this.client;
	}
}
