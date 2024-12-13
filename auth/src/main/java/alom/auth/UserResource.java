package alom.auth;

import org.json.JSONObject;

import alom.auth.models.User;
import alom.auth.services.UserService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class UserResource {

	private UserService userService = new UserService();
	private static final String aller_URL = "http://localhost:8080/aller/webapi/";

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(User user) {
		String nickname = userService.registerUser(user);
		if (nickname != null) {
			return Response.ok(nickname).build();
		} else {
			return Response.status(Response.Status.CONFLICT).entity("User registration failed").build();
		}
	}

	/*@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginUser(User user) {
		String token = userService.verifyCredentials(user.getNickname(), user.getPwd());
		if (token != null) {
			userService.saveUserByToken(user);
			return Response.ok(token).build();

		} else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
		}
	}*/
	
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

	        // Vérifier les informations d'identification
	        String token = userService.verifyCredentials(nickname, password);
	        if (token != null) {
	            // Si l'authentification réussit, enregistrer le token
	            User user = userService.getUserByNickname(nickname);
	            user.setLoginToken(token);
	            userService.saveUserByToken(user);

	            // Retourner le token dans un objet JSON
	            JSONObject responseJson = new JSONObject();
	            responseJson.put("token", token);

	            // Log pour voir le token enregistré
	            System.out.println("Token enregistré: " + token);

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
	@Path("/verify-token")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response verifyToken(@QueryParam("token") String token) {
		if (token == null || token.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Missing token.").build();
		}

		String nickname = userService.verifyToken(token);

		if (nickname != null) {
			return Response.ok("Welcome " + nickname).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("User not found").build();
		}
	}

	@POST
	@Path("/get-name")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnName(String jsonPayload) {
		
	    try {
	        JSONObject json = new JSONObject(jsonPayload);
	        String token = json.getString("token");
	        System.out.println("token reçu de aller : " + token);

	        if (token == null || token.isEmpty()) {
	            return Response.status(Response.Status.BAD_REQUEST).entity("Missing token.").build();
	        } else {
	            User user = userService.getUserByToken(token);
	            if (user != null) {
	                String username = user.getFirstname() + " " + user.getLastname();
	                JSONObject userNameJson = new JSONObject();
	                userNameJson.put("username", username);
	                return Response.status(Response.Status.OK).entity(userNameJson.toString()).build();
	            } else {
	                return Response.status(Response.Status.NOT_FOUND).entity("User not found or not connected.").build();
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur serveur").build();
	    }
	}


	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logoutUser(@QueryParam("nickname") String nickname) {
		boolean loggedOut = userService.logoutUser(nickname);
		if (loggedOut) {
			return Response.ok("Logout successful").build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).entity("User not found").build();
		}
	}
}
