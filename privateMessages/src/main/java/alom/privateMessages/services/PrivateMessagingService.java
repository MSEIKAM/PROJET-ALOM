package alom.privateMessages.services;

import alom.privateMessages.kafka.KafkaMessageProducer;

public class PrivateMessagingService {

    private KafkaTopicService kafkaTopicService;
    private KafkaMessageProducer kafkaMessageProducer;

    public PrivateMessagingService(KafkaTopicService kafkaTopicService, KafkaMessageProducer kafkaMessageProducer) {
        this.kafkaTopicService = kafkaTopicService;
        this.kafkaMessageProducer = kafkaMessageProducer;
    }

    public void sendMessage(String senderName, String receiverNickname, String messageContent) throws Exception {
        // Vérifier si le topic existe et le créer s'il n'existe pas
        kafkaTopicService.createTopic(receiverNickname);

        // Préparer le message (nom du sender + contenu du message)
        String message = senderName + ": " + messageContent;

        // Envoyer le message dans le topic
        kafkaMessageProducer.sendMessage(receiverNickname, senderName, message);
    }
}
