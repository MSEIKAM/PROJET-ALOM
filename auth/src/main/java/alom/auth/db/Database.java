package alom.auth.db;

import alom.auth.models.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    private static final Database INSTANCE = new Database();
    private Map<String, User> users = new ConcurrentHashMap<>();

    private Database() {}

    public static Database getInstance() {
        return INSTANCE;
    }

    public boolean saveUser(User user) {
        if (users.containsKey(user.getNickname())) {
            return false; 
        }
        users.put(user.getNickname(), user);
        return true;
    }

	public User getUserByNickname(String nickname) {
		return users.get(nickname);
	}

	public Collection<User> getAllUsers() {
		return users.values();
	}
}
