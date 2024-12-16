package alom.channels;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.Collections;
import java.util.Properties;

public class KafkaTopicService {

    private AdminClient adminClient;

    public KafkaTopicService() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        this.adminClient = AdminClient.create(props);
    }

    public void createTopic(String topicName) throws Exception {
        NewTopic newTopic = new NewTopic(topicName, 3, (short) 1);
        adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        System.out.println("Topic créé : " + topicName);
    }
}
