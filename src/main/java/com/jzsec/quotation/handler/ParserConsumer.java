package com.jzsec.quotation.handler;

import com.jzsec.quotation.core.client.KafkaClient;
import com.jzsec.quotation.entity.Quotation;
import com.jzsec.quotation.message.Response;
import com.jzsec.quotation.parser.Parser;
import com.jzsec.quotation.parser.QuotationParser;
import com.jzsec.quotation.queue.MsgQueueManager;
import net.sf.json.JSONObject;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.security.JaasUtils;

import java.util.*;

/**
 * Created by caodaoxi on 17-7-17.
 */
public class ParserConsumer implements Runnable {
    private MsgQueueManager msgQueueManager;
    private Map<Integer, Parser> parserMap = new HashMap<Integer, Parser>();
    private XMLConfiguration config;
    private KafkaClient kafkaClient;
    public ParserConsumer() {
    }

    public ParserConsumer(MsgQueueManager msgQueueManager, KafkaClient kafkaClient) {
        this.msgQueueManager = msgQueueManager;
        this.kafkaClient = kafkaClient;
        Parser parser = new QuotationParser();
        this.registerParser(300111, parser);
        this.registerParser(309011, parser);
    }

    public void run() {
        try {
            while (true) {
                Response response = (Response) msgQueueManager.take();
                Object parseVal = null;
                Parser parser = getParser(response.getMsgType());
                if(parser != null) {
                    parseVal = parser.parse(response.getBody());
//                    kafkaClient.send("", JSONObject.fromObject(parseVal).toString());
//                    System.out.println("延迟：" + (System.currentTimeMillis() - ((Quotation)parseVal).getTime().getTime()) + ", " + parseVal);
                } else {
                    System.out.println(response.getMsgType());
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void registerParser(int msgType, Parser parser) {
        parserMap.put(msgType, parser);
    }

    public Parser getParser(int msgType) {
        return parserMap.get(msgType);
    }


}
