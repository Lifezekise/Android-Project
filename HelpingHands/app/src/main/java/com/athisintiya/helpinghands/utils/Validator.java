package com.athisintiya.helpinghands.utils;

import android.util.Patterns;

/**
 * A utility class for validating user input fields.
 */
public class Validator {
    private static final int MAX_FIELD_LENGTH = 100;

    public static boolean isFieldEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean isFieldTooLong(String text) {
        return text != null && text.length() > MAX_FIELD_LENGTH;
    }

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }
}