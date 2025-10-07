package com.athisintiya.helpinghands.utils;

import static com.athisintiya.helpinghands.auth.LoginActivity.KEY_EMAIL;

import android.content.Context;
import android.content.SharedPreferences;
import com.athisintiya.helpinghands.model.User;
import com.athisintiya.helpinghands.repository.UserRepository;

public class SessionManager {
    private static final String PREF_NAME = "HelpingHandsPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private UserRepository userRepository;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        userRepository = UserRepository.getInstance(context);
    }

    public void createLoginSession(User user) {
        if (user != null) {
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_USER_ID, user.getId());
            editor.putString(KEY_USER_NAME, user.getFullName());
            editor.putString(KEY_USER_EMAIL, user.getEmail());
            editor.apply();
            userRepository.setCurrentUser(user);
        }
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
        userRepository.setCurrentUser(null);
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    public void setUserEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}