package com.jzsec.quotation.core.client;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by caodaoxi on 17-7-18.
 */
public class KafkaClient {
    private KafkaProducer<String, String> producer;
    private String topic;

    public KafkaClient(XMLConfiguration config) {
        this.topic = config.getString("kafkaConfig.topic");
        this.producer = getKafkaProducer(config);
    }

    public KafkaProducer getKafkaProducer(XMLConfiguration config) {
        Properties props = new Properties();
        props.put("bootstrap.servers", config.getString("kafkaConfig.brokers"));
        props.put("client.id", config.getString("kafkaConfig.clientId"));
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<String, String>(props);
        return producer;
    }

    public void send(String key, String message) {
       producer.send(new ProducerRecord<String, String>(topic, message));
    }

    public void close() {
        producer.flush();
        producer.close();
    }
}
