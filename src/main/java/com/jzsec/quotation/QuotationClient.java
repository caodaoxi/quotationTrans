package com.jzsec.quotation;

import com.jzsec.quotation.core.client.Client;
import com.jzsec.quotation.proxy.SimpleProxyFactory;
import com.jzsec.quotation.transport.MessagePactTransport;

import java.io.IOException;


/**
 * Created by caodaoxi on 17-7-12.
 */
public class QuotationClient {
    public static void main(String[] args) {
        SimpleProxyFactory factory = new SimpleProxyFactory(new MessagePactTransport());
        try {
            Client client = factory.getClient("10.1.171.191:9999");
            client.connect();
            client.login();

            Thread.sleep(100000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
