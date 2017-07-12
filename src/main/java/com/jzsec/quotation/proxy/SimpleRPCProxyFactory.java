package com.jzsec.quotation.proxy;

import com.gbdex.rpc.client.core.Client;
import com.gbdex.rpc.client.core.netty.SimpleNettyClient;
import com.gbdex.rpc.protocol.namespace.EndpointInfo;
import com.gbdex.rpc.protocol.transport.RPCTransport;
import com.jzsec.quotation.transport.MessagePactTransport;
import com.jzsec.quotation.transport.ProtostuffTransport;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleRPCProxyFactory implements RPCProxyFactroy {

	private StringBuilder serviceEndipont = new StringBuilder();
	private ConcurrentHashMap<String, EndpointInfo> classer = new ConcurrentHashMap<String, EndpointInfo>();
	private ConcurrentHashMap<String, Client> clientPool = new ConcurrentHashMap<String, Client>();
	private ReentrantLock lock = new ReentrantLock(true);
	private RPCTransport transport;

	public SimpleRPCProxyFactory(MessagePactTransport transport) {
		this.transport = transport;
	}

	public void addEnpoint(String... address) {
		for (String addr : address) {
			serviceEndipont.append(addr).append(",");
		}
		if (serviceEndipont.length() != 0) {
			serviceEndipont.delete(serviceEndipont.length() - 1,
					serviceEndipont.length());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> clazz) {
		String name = clazz.getName();
		if (classer.get(name) == null) {
			classer.put(name, new EndpointInfo(-1, serviceEndipont));
		}
		return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class<?>[] { clazz }, new ProxyWarper(name, this));
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> clazz, int timeout) {
		String name = clazz.getName();
		if (classer.get(name) == null) {
			classer.put(name, new EndpointInfo(timeout, serviceEndipont));
		}
		return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class<?>[] { clazz }, new ProxyWarper(name, this));
	}

	public void dispose() throws IOException {
		synchronized (this) {
			Iterator<Map.Entry<String, Client>> it = clientPool.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Client> ent = it.next();
				ent.getValue().close();
			}
		}
	}

	public Client findClient(String endpoint) throws IOException {
		Client c = null;
		try {
			lock.lock();
			c = clientPool.get(endpoint);
			if (c == null) {
				c = new SimpleNettyClient(endpoint, transport);
				c.connect();
				clientPool.put(endpoint, c);
			}
		} finally {
			lock.unlock();
		}
		return c;
	}

	public EndpointInfo findEndpoint(String className) {
		return classer.get(className);
	}
}
