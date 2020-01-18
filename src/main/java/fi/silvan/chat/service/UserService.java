package fi.silvan.chat.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class UserService {
	private Set<String> usernames;

	public UserService() {
		usernames = Collections.synchronizedSet(new HashSet<>());
	}

	public boolean signIn(String username) {
		if (username != null && !username.isBlank()) {
			return usernames.add(username);
		} else {
			throw new IllegalArgumentException("Username must not be null or empty.");
		}
	}

	public void signOut(String username) {
		if (username != null && !username.isBlank()) {
			usernames.remove(username);
		}
	}
}