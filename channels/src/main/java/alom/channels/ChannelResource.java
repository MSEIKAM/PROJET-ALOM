package alom.channels;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;

@Path("/channels")
public class ChannelResource {

    private ChannelService channelService = new ChannelService();

    @POST
    @Path("/join")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response joinChannel(String jsonPayload) {
        try {
            JSONObject json = new JSONObject(jsonPayload);
            String token = json.getString("token");
            String channelName = json.getString("channelName");

            channelService.joinChannel(token, channelName);
            return Response.ok("Rejoint le channel avec succès").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la connexion au channel").build();
        }
    }

    @POST
    @Path("/leave")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response leaveChannel(String jsonPayload) {
        try {
            JSONObject json = new JSONObject(jsonPayload);
            String token = json.getString("token");
            String channelName = json.getString("channelName");

            channelService.leaveChannel(token, channelName);
            return Response.ok("Quitté le channel avec succès").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la sortie du channel").build();
        }
    }

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendMessage(String jsonPayload) {
        try {
            JSONObject json = new JSONObject(jsonPayload);
            String token = json.getString("token");
            String channelName = json.getString("channelName");
            String message = json.getString("message");

            channelService.sendMessage(token, channelName, message);
            return Response.ok("Message envoyé avec succès").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de l'envoi du message").build();
        }
    }
}
