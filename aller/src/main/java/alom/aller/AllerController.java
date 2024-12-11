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
    private static final String AUTH_URL = "http://localhost:8080/auth/webapi/auth";

    private WebTarget target;

    private static final Set<String> registeredUsers = new HashSet<>();

    public AllerController() {
        System.setProperty("java.util.logging.ConsoleHandler.level", "ALL");
        System.setProperty("com.sun.jersey.api.client.filter.LoggingFilter.level", "ALL");
        Client client = ClientBuilder.newClient();
        this.target = client.target(AUTH_URL);
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
            Response response = target.path("register").request().accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(authJson.toString(), MediaType.APPLICATION_JSON));

            System.out.println("Réponse du microservice d'authentification: " + response.getStatus());
            String responseBody = response.readEntity(String.class);
            System.out.println("Corps de la réponse: " + responseBody);

            // Vérifier la réponse du microservice AUTH
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {  //  iliser CREATED
                // Si l'enregistrement réussit, renvoyer la réponse d'AUTH (incluant le nickname
                // généré)
                return Response.status(Response.Status.CREATED).entity(responseBody).build();
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
            Response response = target.path("login").request().accept(MediaType.APPLICATION_JSON)
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

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello() {
        return "Hello, World!";
    }

    @GET
    @Path("/validate-token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateToken(@QueryParam("token") String token) {
        try {
            // Appeler le microservice d'authentification pour valider le token
            Response response = target.path("validate-token").queryParam("token", token)
                    .request().accept(MediaType.APPLICATION_JSON)
                    .get();
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
