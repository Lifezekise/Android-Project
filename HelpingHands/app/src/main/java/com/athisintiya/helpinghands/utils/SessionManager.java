package com.athisintiya.helpinghands.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.athisintiya.helpinghands.model.User;

public class SessionManager {
    private static final String PREF_NAME = "HelpingHandsPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DONATION_COUNT = "donationCount";
    private static final String KEY_LOGIN_TIME = "loginTime";
    private static final String KEY_PROFILE_IMAGE_URL = "profileImageUrl";
    private static final String KEY_PHONE_NUMBER = "phoneNumber";
    private static final String KEY_ADDRESS = "address";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getFullName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PROFILE_IMAGE_URL, user.getProfileImageUrl());
        editor.putString(KEY_PHONE_NUMBER, user.getPhoneNumber());
        editor.putString(KEY_ADDRESS, user.getAddress());
        editor.putInt(KEY_DONATION_COUNT, user.getDonationCount());
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false) && isSessionValid();
    }

    private boolean isSessionValid() {
        long loginTime = pref.getLong(KEY_LOGIN_TIME, 0);
        long currentTime = System.currentTimeMillis();
        long sessionDuration = 24 * 60 * 60 * 1000; // 24 hours

        return (currentTime - loginTime) < sessionDuration;
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, "");
    }

    public String getProfileImageUrl() {
        return pref.getString(KEY_PROFILE_IMAGE_URL, "");
    }

    public String getPhoneNumber() {
        return pref.getString(KEY_PHONE_NUMBER, "");
    }

    public String getAddress() {
        return pref.getString(KEY_ADDRESS, "");
    }

    public int getDonationCount() {
        return pref.getInt(KEY_DONATION_COUNT, 0);
    }

    // Get complete User object
    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        User user = new User();
        user.setId(pref.getString(KEY_USER_ID, ""));
        user.setFullName(pref.getString(KEY_USER_NAME, ""));
        user.setEmail(pref.getString(KEY_EMAIL, ""));
        user.setProfileImageUrl(pref.getString(KEY_PROFILE_IMAGE_URL, ""));
        user.setPhoneNumber(pref.getString(KEY_PHONE_NUMBER, ""));
        user.setAddress(pref.getString(KEY_ADDRESS, ""));
        user.setDonationCount(pref.getInt(KEY_DONATION_COUNT, 0));

        return user;
    }

    public void updateUserDetails(User user) {
        editor.putString(KEY_USER_NAME, user.getFullName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PROFILE_IMAGE_URL, user.getProfileImageUrl());
        editor.putString(KEY_PHONE_NUMBER, user.getPhoneNumber());
        editor.putString(KEY_ADDRESS, user.getAddress());
        editor.apply();
    }

    public void updateUserProfile(String name, String email, String phone, String address) {
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE_NUMBER, phone);
        editor.putString(KEY_ADDRESS, address);
        editor.apply();
    }

    public void updateProfileImage(String imageUrl) {
        editor.putString(KEY_PROFILE_IMAGE_URL, imageUrl);
        editor.apply();
    }

    public void incrementDonationCount() {
        int currentCount = getDonationCount();
        editor.putInt(KEY_DONATION_COUNT, currentCount + 1);
        editor.apply();
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    public void updateLoginTime() {
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.apply();
    }

    // Check if user profile is complete
    public boolean isProfileComplete() {
        return !getUserName().isEmpty() &&
                !getEmail().isEmpty() &&
                !getPhoneNumber().isEmpty() &&
                !getAddress().isEmpty();
    }
}