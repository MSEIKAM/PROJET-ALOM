package alom.auth.db;

import alom.auth.models.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    private static final Database INSTANCE = new Database();
    private Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> usersByToken = new ConcurrentHashMap<>();

    private Database() {}

    public static Database getInstance() {
        return INSTANCE;
    }
    public boolean saveUser(User user) {
        if (userExists(user)) {
            return false; 
        }
        users.put(user.getNickname(), user);
        System.out.println("User saved: " + user.toString());
        return true;
    }

   /* public boolean saveUser(User user) {
        if (users.containsKey(user.getNickname())) {
            return false; 
        }
        users.put(user.getNickname(), user);
        return true;
    }*/

	public User getUserByNickname(String nickname) {
		return users.get(nickname);
	}
	public User getUserByToken(String token)
	
	{
		return usersByToken.get(token);	}
	
	public boolean saveToken(User user) {
		usersByToken.put(user.getLoginToken(),user);
		return true;
		
	}
    public boolean userExists(User user) {
        for (User existingUser : users.values()) {
        	
            if (existingUser.getFirstname().equals(user.getFirstname()) && 
                existingUser.getLastname().equals(user.getLastname())
            	&& existingUser.getNickname().equals(user.getNickname()))
            {
                return true; // 
            }
        }
        return false;
    }

	public Collection<User> getAllUsers() {
		return users.values();
	}
}
