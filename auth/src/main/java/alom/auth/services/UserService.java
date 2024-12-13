package alom.auth.services;

import alom.auth.models.User;
import alom.auth.db.Database;

import java.util.UUID;

public class UserService {

    private Database database = Database.getInstance();

    public String registerUser(User user) {
        
        String nickname = user.getFirstname() + "-" + UUID.randomUUID().toString().substring(0, 5);
        user.setNickname(nickname);

       
        boolean isSaved = database.saveUser(user);
        return isSaved ? nickname : null;
    }

	public String verifyCredentials(String nickname, String pwd) {
		User user = database.getUserByNickname(nickname);
        if (user != null && user.getPwd().equals(pwd)) {
        	String token = UUID.randomUUID().toString();
        	user.setLoginToken(token); 
        	 return token;
        }
        return null;
	}
	
	public boolean logoutUser(String nickname) {
        
        User user = database.getUserByNickname(nickname);
        if (user != null) {
            user.clearLoginToken();  
            return true;  
        }
        return false; 
    }

	public String verifyToken(String token) {
		for (User user : database.getAllUsers()) {
	        if (token.equals(user.getLoginToken())) {
	            return user.getNickname();
	        }
	    }
	    return null;
	}
	public void saveUserByToken(User user) {
		database.saveToken(user);
		
	}
	public User getUserByToken( String token) {
		return database.getUserByToken(token);
	}
	public User getUserByNickname( String nickname) {
		return database.getUserByNickname(nickname);
	}
}
