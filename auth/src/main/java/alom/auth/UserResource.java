package alom.auth;

import alom.auth.models.User;
import alom.auth.services.UserService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class UserResource {

    private UserService userService = new UserService();

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
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(User user) {
    	String token = userService.verifyCredentials(user.getNickname(), user.getPwd());
        if (token != null) {
            return Response.ok(token).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
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
