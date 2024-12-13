package alom.privateMessages;

import alom.privateMessages.services.PrivateMessagingService;
import alom.privateMessages.services.KafkaTopicService;
import alom.privateMessages.kafka.KafkaMessageProducer;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;

@Path("/messages")
public class PrivateMessagesResource {

    private PrivateMessagingService messagingService;

    public PrivateMessagesResource() {
        KafkaTopicService kafkaTopicService = new KafkaTopicService();
        KafkaMessageProducer kafkaMessageProducer = new KafkaMessageProducer();
        this.messagingService = new PrivateMessagingService(kafkaTopicService, kafkaMessageProducer);
    }

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendMessage(String jsonPayload) {
        try {
            // Récupérer les données de la requête JSON
            JSONObject messageJson = new JSONObject(jsonPayload);
            String senderName = messageJson.getString("senderName");
            String receiverNickname = messageJson.getString("receiverNickname");
            String messageContent = messageJson.getString("messageContent");

            // Envoyer le message via le service de messagerie
            messagingService.sendMessage(senderName, receiverNickname, messageContent);

            return Response.ok("Message envoyé avec succès").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Échec de l'envoi du message: " + e.getMessage()).build();
        }
    }
}

