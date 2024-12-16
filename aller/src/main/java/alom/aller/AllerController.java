package alom.aller;

import org.json.JSONObject;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

@Path("/")
public class AllerController {

	// URL du microservice d'authentification
	private static final String AUTH_URL = "http://localhost:8080/auth/webapi/auth/";
	private static final String PrivateMessages_URL = "http://localhost:8080/privateMessages/webapi/";

	private WebTarget targetAuth;
	private WebTarget targetPrivateMessages;

	private static final Set<String> registeredUsers = new HashSet<>();

	public AllerController() {
		System.setProperty("java.util.logging.ConsoleHandler.level", "ALL");
		System.setProperty("com.sun.jersey.api.client.filter.LoggingFilter.level", "ALL");
		Client client = ClientBuilder.newClient();
		this.targetAuth = client.target(AUTH_URL);
		this.targetPrivateMessages = client.target(PrivateMessages_URL);
		// Log pour indiquer que le client est prêt
		System.out.println("Client Jersey configuré avec URL : " + AUTH_URL);
	}

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(String jsonPayload) {
		try {
			// Récupérer les données de l'utilisateur (firstname, lastname, password)
			JSONObject userJson = new JSONObject(jsonPayload);
			String firstName = userJson.getString("firstname");
			String lastName = userJson.getString("lastname");
			String password = userJson.getString("pwd");

			// Créer un objet JSON pour l'utilisateur à envoyer au microservice AUTH
			JSONObject authJson = new JSONObject();
			authJson.put("firstname", firstName);
			authJson.put("lastname", lastName);
			authJson.put("pwd", password);

			System.out.println("Requête envoyée au microservice : " + authJson.toString());

			// Appeler le microservice d'authentification pour enregistrer l'utilisateur
			Response response = targetAuth.path("register").request().accept(MediaType.APPLICATION_JSON)
					.post(Entity.entity(authJson.toString(), MediaType.APPLICATION_JSON));

			System.out.println("Réponse du microservice d'authentification: " + response.getStatus());
			String responseBody = response.readEntity(String.class);
			System.out.println("Corps de la réponse: " + responseBody);

			// Vérifier la réponse du microservice AUTH
			if (response.getStatus() == Response.Status.OK.getStatusCode()) { // Utiliser CREATED
				// Si l'enregistrement réussit, renvoyer la réponse d'AUTH (incluant le nickname
				// généré)
				return Response.status(Response.Status.OK).entity(responseBody).build();
			} else {
				return Response.status(Response.Status.CONFLICT).entity("Utilisateur déjà existant").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur serveur").build();
		}
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginUser(String jsonPayload) {
		try {
			// Récupérer les données de l'utilisateur (nickname, password)
			JSONObject userJson = new JSONObject(jsonPayload);
			String nickname = userJson.getString("nickname");
			String password = userJson.getString("pwd");

			// Log pour voir les données reçues
			System.out.println("Données reçues pour la connexion: nickname = " + nickname + ", password = " + password);

			// Créer un objet JSON pour l'authentification
			JSONObject authJson = new JSONObject();
			authJson.put("nickname", nickname);
			authJson.put("pwd", password);

			// Log pour voir les données envoyées au service auth
			System.out.println("Requête de connexion envoyée au microservice : " + authJson.toString());

			// Appeler le microservice d'authentification pour vérifier les informations
			Response response = targetAuth.path("login").request().accept(MediaType.APPLICATION_JSON)
					.post(Entity.entity(authJson.toString(), MediaType.APPLICATION_JSON));

			// Log pour voir le statut de la réponse
			System.out.println("Réponse du microservice d'authentification: " + response.getStatus());
			String responseBody = response.readEntity(String.class);
			System.out.println("Corps de la réponse: " + responseBody);

			// Vérifier la réponse du microservice d'authentification
			if (response.getStatus() == 200) {
				// Si l'authentification réussit, traiter le token reçu comme une chaîne de
				// caractères simple
				String token = responseBody; // Traiter la réponse comme une chaîne simple

				// Log pour voir le token reçu
				System.out.println("Token reçu: " + token);

				// Retourner le token dans un objet JSON
				JSONObject responseJson = new JSONObject();
				responseJson.put("token", token);
				return Response.ok(responseJson.toString()).build();
			} else {
				System.out.println("Login ou mot de passe incorrect");
				return Response.status(Response.Status.UNAUTHORIZED).entity("Login ou mot de passe incorrect").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur serveur").build();
		}
	}

	@POST
	@Path("/send-message")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendMessage(String jsonPayload) {
		System.out.println("Reçu payload : " + jsonPayload);

		try {
			JSONObject messageJson = new JSONObject(jsonPayload);
			String token = messageJson.getString("token");
			String receiver = messageJson.getString("receiver");
			String messageContent = messageJson.getString("message");

			// Créer un objet JSON pour envoyer le token à /get-name
			JSONObject tokenJson = new JSONObject();
			tokenJson.put("token", token);

			// Appeler le microservice d'authentification pour récupérer le nom de
			// l'utilisateur
			System.out.println("Envoi du token pour récupération du username : " + token);
			Response response = targetAuth.path("/get-name").request().accept(MediaType.APPLICATION_JSON)
					.post(Entity.entity(tokenJson.toString(), MediaType.APPLICATION_JSON));
			String responseBody = response.readEntity(String.class);

			System.out.println("Réponse de l'authentification : " + response.getStatus() + " - " + responseBody);

			if (response.getStatus() == 200) {
				JSONObject responseJson = new JSONObject(responseBody);
				String senderName = responseJson.getString("username");
				System.out.println("Username récupéré : " + senderName);

				// Créer un objet JSON pour envoyer au microservice PrivateMessages
				JSONObject privateMessageJson = new JSONObject();
				privateMessageJson.put("senderName", senderName);
				privateMessageJson.put("receiverNickname", receiver);
				privateMessageJson.put("messageContent", messageContent);
				System.out.println("Données envoyées à PrivateMessages : " + privateMessageJson.toString());

				// Appeler le microservice PrivateMessages pour envoyer le message
				Response messageResponse = targetPrivateMessages.path("/messages/send").request()
						.accept(MediaType.TEXT_PLAIN)
						.post(Entity.entity(privateMessageJson.toString(), MediaType.APPLICATION_JSON));

				System.out.println("Réponse de PrivateMessages : " + messageResponse.getStatus());

				if (messageResponse.getStatus() == 200) {
					System.out.println("Message envoyé avec succès.");
					return Response.ok("Message envoyé avec succès").build();
				} else {
					System.out.println(
							"Erreur lors de l'envoi du message : " + messageResponse.getStatusInfo().toString());
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity("Erreur lors de l'envoi du message").build();
				}
			} else {
				System.out.println("Utilisateur non connecté ou non trouvé : " + responseBody);
				return Response.status(Response.Status.UNAUTHORIZED).entity("Utilisateur non connecté").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Erreur serveur : " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur serveur").build();
		}
	}

	@POST
	@Path("/join-channel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response joinChannel(String jsonPayload) {
		try {
			Client client = ClientBuilder.newClient();
			WebTarget targetChannels = client.target("http://localhost:8080/channels/webapi/channels/join");
			Response response = targetChannels.request().post(Entity.json(jsonPayload));
			return Response.status(response.getStatus()).entity(response.readEntity(String.class)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur interne").build();
		}
	}

	@POST
	@Path("/send-channel-message")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response sendChannelMessage(String jsonPayload) {
		try {
			Client client = ClientBuilder.newClient();
			WebTarget targetChannels = client.target("http://localhost:8080/channels/webapi/channels/send");
			Response response = targetChannels.request().post(Entity.json(jsonPayload));
			return Response.status(response.getStatus()).entity(response.readEntity(String.class)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur interne").build();
		}
	}

	@GET
	@Path("/validate-token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateToken(@QueryParam("token") String token) {
		try {
			// Appeler le microservice d'authentification pour valider le token
			Response response = targetAuth.path("validate-token").queryParam("token", token).request()
					.accept(MediaType.APPLICATION_JSON).get();
			// Vérifier la réponse du microservice d'authentification
			if (response.getStatus() == 200) {
				// Si le token est valide, renvoyer le nickname associé
				String responseBody = response.readEntity(String.class);
				return Response.ok(responseBody).build();
			} else {
				return Response.status(Response.Status.UNAUTHORIZED).entity("Token invalide").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur serveur").build();
		}
	}

}
