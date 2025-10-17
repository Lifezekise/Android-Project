package com.athisintiya.helpinghands.model;

public class User {
    private String fullName;
    private String email;
    private String profileImageUrl;
    private String id;
    private int donationCount;
    private String phoneNumber;
    private String address;

    public User() {
        // Default constructor required for Firestore
        this.donationCount = 0;
    }

    public User(String fullName, String email, String profileImageUrl, String id) {
        this.fullName = fullName;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.id = id;
        this.donationCount = 0;
    }

    public User(String fullName, String email, String profileImageUrl, String id, int donationCount) {
        this.fullName = fullName;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.id = id;
        this.donationCount = donationCount;
    }

    // Getters and setters
    public String getFullName() {
        return fullName != null ? fullName : "";
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl != null ? profileImageUrl : "";
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDonationCount() {
        return donationCount;
    }

    public void setDonationCount(int donationCount) {
        this.donationCount = donationCount;
    }

    public void incrementDonationCount() {
        this.donationCount++;
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber : "";
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Helper methods
    public String getUserId() {
        return getId();
    }

    public boolean isValid() {
        return !getFullName().isEmpty() && !getEmail().isEmpty() && !getId().isEmpty();
    }

    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                ", donationCount=" + donationCount +
                '}';
    }
}