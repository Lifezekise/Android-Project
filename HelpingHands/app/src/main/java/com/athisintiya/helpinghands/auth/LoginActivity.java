package com.athisintiya.helpinghands.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.dashboard.MainActivity;
import com.athisintiya.helpinghands.databinding.ActivityLoginBinding;
import com.athisintiya.helpinghands.model.User;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;
import com.athisintiya.helpinghands.utils.Validator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private NavigationHelper navigationHelper;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "LoginActivity";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_GUEST_MODE = "guest_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationHelper = new NavigationHelper(this);
        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupClickListeners();
        restoreSavedInstanceState(savedInstanceState);
    }

    private void setupClickListeners() {
        if (binding.btnLogin != null) {
            binding.btnLogin.setOnClickListener(v -> handleLogin());
        }
        if (binding.tvSignupLink != null) {
            binding.tvSignupLink.setOnClickListener(v -> navigationHelper.navigateToSignup());
        }
        if (binding.tvForgotUsername != null) {
            binding.tvForgotUsername.setOnClickListener(v -> showForgotUsernameDialog());
        }
        if (binding.tvForgotPassword != null) {
            binding.tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
        }
        if (binding.emergencyHelp != null) {
            binding.emergencyHelp.setOnClickListener(v -> handleEmergencyHelp());
        }
        if (binding.guestMode != null) {
            binding.guestMode.setOnClickListener(v -> handleGuestMode());
        }
    }

    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String savedEmail = savedInstanceState.getString(KEY_EMAIL, "");
            String savedPassword = savedInstanceState.getString(KEY_PASSWORD, "");
            boolean wasGuestMode = savedInstanceState.getBoolean(KEY_GUEST_MODE, false);
            if (binding.etEmail != null) binding.etEmail.setText(savedEmail);
            if (binding.etPassword != null) binding.etPassword.setText(savedPassword);
            if (wasGuestMode) {
                updateSubtitleForGuestMode();
                handleGuestMode();
            } else if (binding.subtitle != null) {
                binding.subtitle.setText("Sign in to continue making a difference");
            }
        }
    }

    private void updateSubtitleForGuestMode() {
        if (binding.subtitle != null) {
            binding.subtitle.setText("Exploring as a guest? Sign in for full access!");
        }
    }

    private void handleLogin() {
        String email = binding.etEmail != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword != null ? binding.etPassword.getText().toString().trim() : "";

        if (!validateInputs(email, password)) {
            return;
        }

        showLoading(true);
        if (binding.subtitle != null) {
            binding.subtitle.setText("Logging in...");
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (binding.subtitle != null) {
                        binding.subtitle.setText("Sign in to continue making a difference");
                    }

                    if (task.isSuccessful()) {
                        handleSuccessfulLogin();
                    } else {
                        handleLoginError(task.getException());
                    }
                });
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            if (binding.emailLayout != null) binding.emailLayout.setError("Email is required");
            isValid = false;
        } else if (!Validator.isValidEmail(email)) {
            if (binding.emailLayout != null) binding.emailLayout.setError("Please enter a valid email address");
            isValid = false;
        } else {
            if (binding.emailLayout != null) binding.emailLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            if (binding.passwordLayout != null) binding.passwordLayout.setError("Password is required");
            isValid = false;
        } else if (!Validator.isValidPassword(password)) {
            if (binding.passwordLayout != null) binding.passwordLayout.setError("Password must be at least 8 characters");
            isValid = false;
        } else {
            if (binding.passwordLayout != null) binding.passwordLayout.setError(null);
        }

        return isValid;
    }

    private void handleSuccessfulLogin() {
        com.google.firebase.auth.FirebaseUser fbUser = mAuth.getCurrentUser();

        if (fbUser == null) {
            showErrorDialog("Authentication failed. Please try again.");
            return;
        }

        if (!fbUser.isEmailVerified()) {
            showEmailVerificationDialog(fbUser);
            return;
        }

        fetchUserDataFromFirestore(fbUser.getUid());
    }

    private void showEmailVerificationDialog(com.google.firebase.auth.FirebaseUser fbUser) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Email Verification Required")
                .setMessage("Please verify your email address before logging in. Would you like us to resend the verification email?")
                .setPositiveButton("Resend", (dialog, which) -> {
                    showLoading(true);
                    if (binding.subtitle != null) {
                        binding.subtitle.setText("Sending verification email...");
                    }
                    fbUser.sendEmailVerification()
                            .addOnCompleteListener(task -> {
                                showLoading(false);
                                if (binding.subtitle != null) {
                                    binding.subtitle.setText("Sign in to continue making a difference");
                                }
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void fetchUserDataFromFirestore(String userId) {
        showLoading(true);
        if (binding.subtitle != null) {
            binding.subtitle.setText("Fetching user data...");
        }

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    showLoading(false);
                    if (binding.subtitle != null) {
                        binding.subtitle.setText("Sign in to continue making a difference");
                    }
                    if (documentSnapshot.exists()) {
                        User appUser = documentSnapshot.toObject(User.class);
                        if (appUser != null) {
                            handleUserDataSuccess(appUser);
                        } else {
                            showErrorDialog("User data corrupted. Please contact support.");
                        }
                    } else {
                        showErrorDialog("User account not found. Please sign up first.");
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    if (binding.subtitle != null) {
                        binding.subtitle.setText("Sign in to continue making a difference");
                    }
                    Log.e(TAG, "Firestore error: " + e.getMessage());
                    showErrorDialog("Failed to load user data. Please try again.");
                });
    }

    private void handleUserDataSuccess(User appUser) {
        sessionManager.createLoginSession(appUser);

        // Subscribe to notifications for Cape Town area
        subscribeToNotifications();

        // Navigate to main activity
        navigationHelper.navigateToMain();
        finish();
    }

    private void subscribeToNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("cape_town_help")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to Cape Town notifications");
                    } else {
                        Log.e(TAG, "Failed to subscribe to notifications");
                    }
                });
    }

    private void handleEmergencyHelp() {
        showLoading(true);
        if (binding.subtitle != null) {
            binding.subtitle.setText("Accessing emergency resources...");
        }
        navigationHelper.navigateToResources();
        showLoading(false);
        if (binding.subtitle != null) {
            binding.subtitle.setText("Sign in to continue making a difference");
        }
        Toast.makeText(this, "Accessing emergency resources...", Toast.LENGTH_SHORT).show();
    }

    private void handleGuestMode() {
        showLoading(true);
        updateSubtitleForGuestMode();

        // Create a guest user session
        User guestUser = new User("Guest", "guest@helpinghands.com", null, "GUEST");
        sessionManager.createLoginSession(guestUser);

        // Subscribe to notifications for guest mode
        FirebaseMessaging.getInstance().subscribeToTopic("cape_town_help")
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Guest mode: Subscribed to Cape Town notifications");
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("isGuest", true);
                        startActivity(intent);
                        Toast.makeText(this, "Entered guest mode", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e(TAG, "Guest mode: Failed to subscribe to notifications");
                        if (binding.subtitle != null) {
                            binding.subtitle.setText("Sign in to continue making a difference");
                        }
                        showErrorDialog("Failed to enter guest mode. Please try again.");
                    }
                });
    }

    private void handleLoginError(Exception exception) {
        showLoading(false);
        if (binding.subtitle != null) {
            binding.subtitle.setText("Sign in to continue making a difference");
        }
        String errorMessage = "Login failed. Please try again.";

        if (exception instanceof FirebaseAuthInvalidUserException) {
            errorMessage = "No account found with this email. Please sign up.";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = "Invalid password. Please try again.";
        } else if (exception instanceof FirebaseFirestoreException) {
            errorMessage = "Network error. Please check your connection.";
        } else if (exception != null) {
            errorMessage = "Authentication error: " + exception.getMessage();
        }

        Log.e(TAG, "Login error: " + (exception != null ? exception.getMessage() : "Unknown error"));
        showErrorDialog(errorMessage);
    }

    private void showForgotUsernameDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Forgot Username?")
                .setMessage("Your username is your email address. Please use the 'Forgot Password' option if you can't remember your password.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showForgotPasswordDialog() {
        String email = binding.etEmail != null ? binding.etEmail.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email) || !Validator.isValidEmail(email)) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Forgot Password")
                    .setMessage("Please enter a valid email address first")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        showLoading(true);
        if (binding.subtitle != null) {
            binding.subtitle.setText("Sending password reset email...");
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (binding.subtitle != null) {
                        binding.subtitle.setText("Sign in to continue making a difference");
                    }

                    if (task.isSuccessful()) {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Password Reset")
                                .setMessage("Password reset link has been sent to your email")
                                .setPositiveButton("OK", null)
                                .show();
                    } else {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Error")
                                .setMessage("Failed to send reset email. Please check your email address.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });
    }

    private void showLoading(boolean show) {
        if (binding.btnLogin != null) {
            binding.btnLogin.setEnabled(!show);
            binding.btnLogin.setText(show ? "" : "Log In");
        }
        if (binding.emergencyHelp != null) {
            binding.emergencyHelp.setEnabled(!show);
        }
        if (binding.guestMode != null) {
            binding.guestMode.setEnabled(!show);
        }
        if (binding.tvForgotUsername != null) {
            binding.tvForgotUsername.setEnabled(!show);
        }
        if (binding.tvForgotPassword != null) {
            binding.tvForgotPassword.setEnabled(!show);
        }
        if (binding.tvSignupLink != null) {
            binding.tvSignupLink.setEnabled(!show);
        }
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showErrorDialog(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (binding.etEmail != null) {
            outState.putString(KEY_EMAIL, binding.etEmail.getText().toString());
        }
        if (binding.etPassword != null) {
            outState.putString(KEY_PASSWORD, binding.etPassword.getText().toString());
        }
        outState.putBoolean(KEY_GUEST_MODE, sessionManager.getCurrentUser() != null && "GUEST".equals(sessionManager.getCurrentUser().getId()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}