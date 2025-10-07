package com.athisintiya.helpinghands.repository;

import android.content.Context;

import com.athisintiya.helpinghands.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private static UserRepository instance;
    private final Map<String, User> users;
    private User currentUser;

    private UserRepository(Context context) {
        users = new HashMap<>();
        registerUser(new User("Test User", "test@example.com", "password123", "test@example.com"));
    }

    public static synchronized UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context);
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void registerUser(User user) {
        users.put(user.getEmail(), user);
    }

    public boolean authenticate(String email, String password) {
        if (users.containsKey(email)) {
            User user = users.get(email);
            return user.getPassword().equals(hashPassword(password));
        }
        return false;
    }

    public User getUserByEmail(String email) {
        return users.get(email);
    }

    public boolean recordDonation(String email) {
        User user = getUserByEmail(email);
        if (user != null) {
            user.incrementDonationCount();
            return true;
        }
        return false;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }
}