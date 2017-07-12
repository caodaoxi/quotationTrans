package com.jzsec.quotation.proxy;

import com.gbdex.rpc.client.core.Client;
import com.gbdex.rpc.protocol.namespace.EndpointInfo;

import java.io.IOException;

public interface RPCProxyFactroy {
	// 注册服务端地址
	public void addEnpoint(String... address);

	// 创建同步代理实现
	public <T> T create(Class<T> clazz);

	// 创建同步代理实现,可以自定义超时时间
	public <T> T create(Class<T> clazz, int timeout);

	// 释放资源
	public void dispose() throws IOException;

	public EndpointInfo findEndpoint(String className);

	public Client findClient(String endpoint) throws IOException;

}
