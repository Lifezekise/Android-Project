package com.athisintiya.helpinghands.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A model class representing a user.
 */
public class User {
    private String id;
    private String fullName;
    private String email;
    private String password; // Hashed password (for demo purposes)
    private int donationCount;
    private boolean active;

    public User(String fullName, String email, String password, String id) {
        this.fullName = fullName;
        this.email = email;
        this.password = hashPassword(password); // Hash password
        this.id = id;
        this.donationCount = 0;
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getDonationCount() {
        return donationCount;
    }

    public void incrementDonationCount() {
        this.donationCount++;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Simple password hashing for demo purposes (use BCrypt in production)
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
            return password; // Fallback (not secure)
        }
    }
}