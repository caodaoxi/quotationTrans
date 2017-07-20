package com.jzsec.quotation;

import com.jzsec.quotation.core.client.Client;
import com.jzsec.quotation.proxy.SimpleProxyFactory;
import com.jzsec.quotation.transport.MessagePactTransport;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * Created by caodaoxi on 17-7-12.
 */
public class QuotationClient {
    public static void main(String[] args) {
        try {
            if(args.length < 1) {
                throw new Exception("quotation config file Not specified");
            }
            XMLConfiguration config = new XMLConfiguration(args[0]);
            SimpleProxyFactory factory = new SimpleProxyFactory(new MessagePactTransport(), config);
            Client client = factory.getClient();
            client.connect();
            client.login(10);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
