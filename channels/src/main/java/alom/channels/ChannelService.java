package alom.channels;

public class ChannelService {
    private KafkaTopicService kafkaTopicService = new KafkaTopicService();
    private KafkaMessageProducer kafkaMessageProducer = new KafkaMessageProducer();

    public void joinChannel(String token, String channelName) throws Exception {
        kafkaTopicService.createTopic(channelName);
        System.out.println("Utilisateur rejoint le channel : " + channelName + " avec le token : " + token);
    }

    public void leaveChannel(String token, String channelName) {
        System.out.println("Utilisateur quitté le channel : " + channelName + " avec le token : " + token);
    }

    public void sendMessage(String token, String channelName, String messageContent) throws Exception {
        String userName = getUserFromToken(token);
        if (userName != null) {
            String message = userName + ": " + messageContent;
            kafkaMessageProducer.sendMessage(channelName, userName, message);
        } else {
            throw new Exception("Utilisateur non valide pour le token : " + token);
        }
    }

    private String getUserFromToken(String token) {
        try {
            // Appel à l'authentification pour récupérer le nom d'utilisateur
            return "MockedUser"; // Remplacez par un appel réel à Auth
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
