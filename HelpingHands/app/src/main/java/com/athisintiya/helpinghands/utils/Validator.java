package com.athisintiya.helpinghands.utils;

import android.text.TextUtils;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && name.trim().length() <= 50;
    }

    public static boolean isValidQuantity(String quantity) {
        if (TextUtils.isEmpty(quantity)) return false;
        try {
            int qty = Integer.parseInt(quantity);
            return qty > 0 && qty <= 1000;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidAmount(String amount) {
        if (TextUtils.isEmpty(amount)) return false;
        try {
            double amt = Double.parseDouble(amount);
            return amt > 0 && amt <= 100000;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}