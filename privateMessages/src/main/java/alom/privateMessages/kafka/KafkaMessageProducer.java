package alom.privateMessages.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import jakarta.inject.Inject;

import java.util.Properties;


public class KafkaMessageProducer {

    private KafkaProducer<String, String> producer;

    @Inject
    public KafkaMessageProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(props);
    }

    public void sendMessage(String topicName, String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, value);
        producer.send(record);
        System.out.println("Message envoyé avec succès dans le topic : " + topicName);
    }
}

