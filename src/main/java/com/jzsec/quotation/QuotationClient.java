package com.jzsec.quotation;

import com.jzsec.quotation.proxy.SimpleRPCProxyFactory;
import com.jzsec.quotation.transport.MessagePactTransport;


/**
 * Created by caodaoxi on 17-7-12.
 */
public class QuotationClient {
    public static void main(String[] args) {
        SimpleRPCProxyFactory factory = new SimpleRPCProxyFactory(new MessagePactTransport());
        factory.addEnpoint("10.1.171.191:9999");

    }
}
