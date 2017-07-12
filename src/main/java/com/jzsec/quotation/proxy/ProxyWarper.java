package com.jzsec.quotation.proxy;

import com.gbdex.rpc.client.core.netty.SimpleNettyClient;
import com.gbdex.rpc.protocol.message.Request;
import com.gbdex.rpc.protocol.message.Response;
import com.gbdex.rpc.protocol.namespace.EndpointInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ProxyWarper {

	private String className;
	private RPCProxyFactroy factory;

	public ProxyWarper(String className, RPCProxyFactroy factory) {
		this.className = className;
		this.factory = factory;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Request request = new Request();
		request.setClassName(className);
		request.setMethodName(method.getName());
		if (args != null) {
			StringBuilder parmaTyps = new StringBuilder();
			List<Object> parmaValue = new LinkedList<Object>();
			for (Object o : args) {
				parmaTyps.append(o.getClass().getName()).append(",");
				parmaValue.add(o);
			}
			parmaTyps.delete(parmaTyps.length() - 1, parmaTyps.length());
			request.setParamTypes(parmaTyps.toString());
			request.setParamValues(parmaValue);
			parmaTyps = null;
		}
		EndpointInfo endpoint = factory.findEndpoint(className);
		SimpleNettyClient client = (SimpleNettyClient) factory
				.findClient(endpoint.getEndpoint().toString());
		Response resp = client.invoke(request, endpoint.getTimeout());
		return resp.getResponseEntry();
	}

}
