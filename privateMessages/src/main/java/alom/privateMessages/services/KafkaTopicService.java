package alom.privateMessages.services;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.ListTopicsResult;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaTopicService {

    private AdminClient adminClient;

    public KafkaTopicService() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        this.adminClient = AdminClient.create(props);
    }

    public void createTopic(String topicName) throws Exception {
        if (!topicExists(topicName)) {
            NewTopic newTopic = new NewTopic(topicName, 3, (short) 1);
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
            System.out.println("Topic créé avec succès : " + topicName);
        } else {
            System.out.println("Le topic " + topicName + " existe déjà.");
        }
    }

    private boolean topicExists(String topicName) throws ExecutionException, InterruptedException {
        ListTopicsResult topics = adminClient.listTopics();
        return topics.names().get().contains(topicName);
    }
}
