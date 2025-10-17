package com.athisintiya.helpinghands.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.athisintiya.helpinghands.databinding.ActivitySignupBinding;
import com.athisintiya.helpinghands.model.User;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;
import com.athisintiya.helpinghands.utils.Validator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private NavigationHelper navigationHelper;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "SignupActivity";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_CONFIRM_PASSWORD = "confirm_password";
    private static final int MAX_NAME_LENGTH = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeComponents();
        setupClickListeners();
        restoreSavedInstanceState(savedInstanceState);
    }

    private void initializeComponents() {
        navigationHelper = new NavigationHelper(this);
        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void setupClickListeners() {
        if (binding.btnSignup != null) {
            binding.btnSignup.setOnClickListener(v -> registerUser());
        }
        if (binding.tvLoginLink != null) {
            binding.tvLoginLink.setOnClickListener(v -> navigationHelper.navigateToLogin());
        }
    }

    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            safelySetText(binding.etFullName, savedInstanceState.getString(KEY_FULL_NAME, ""));
            safelySetText(binding.etUsername, savedInstanceState.getString(KEY_EMAIL, ""));
            safelySetText(binding.etPassword, savedInstanceState.getString(KEY_PASSWORD, ""));
            safelySetText(binding.etConfirmPassword, savedInstanceState.getString(KEY_CONFIRM_PASSWORD, ""));
            safelySetChecked(binding.cbTerms, savedInstanceState.getBoolean("terms", false));
            safelySetChecked(binding.cbNotRobot, savedInstanceState.getBoolean("not_robot", false));
            if (binding.subtitle != null) {
                binding.subtitle.setText("Create an account to make a difference");
            }
        }
    }

    private void safelySetText(com.google.android.material.textfield.TextInputEditText editText, String text) {
        if (editText != null && text != null) {
            editText.setText(text);
        }
    }

    private void safelySetChecked(android.widget.CheckBox checkBox, boolean checked) {
        if (checkBox != null) {
            checkBox.setChecked(checked);
        }
    }

    private void registerUser() {
        String fullName = safelyGetText(binding.etFullName);
        String email = safelyGetText(binding.etUsername);
        String password = safelyGetText(binding.etPassword);
        String confirmPassword = safelyGetText(binding.etConfirmPassword);

        if (!validateInputs(fullName, email, password, confirmPassword)) {
            return;
        }

        showLoading(true);
        if (binding.subtitle != null) {
            binding.subtitle.setText("Creating your account...");
        }
        createFirebaseUser(email, password, fullName);
    }

    private String safelyGetText(com.google.android.material.textfield.TextInputEditText editText) {
        if (editText == null || editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private boolean validateInputs(String fullName, String email, String password, String confirmPassword) {
        boolean isValid = true;

        // Validate full name
        if (TextUtils.isEmpty(fullName)) {
            if (binding.fullNameLayout != null) binding.fullNameLayout.setError("Full name is required");
            isValid = false;
        } else if (fullName.length() > MAX_NAME_LENGTH) {
            if (binding.fullNameLayout != null) binding.fullNameLayout.setError("Full name must be less than " + MAX_NAME_LENGTH + " characters");
            isValid = false;
        } else if (!Validator.isValidName(fullName)) {
            if (binding.fullNameLayout != null) binding.fullNameLayout.setError("Please enter a valid name");
            isValid = false;
        } else {
            if (binding.fullNameLayout != null) binding.fullNameLayout.setError(null);
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            if (binding.usernameLayout != null) binding.usernameLayout.setError("Email is required");
            isValid = false;
        } else if (!Validator.isValidEmail(email)) {
            if (binding.usernameLayout != null) binding.usernameLayout.setError("Please enter a valid email address");
            isValid = false;
        } else {
            if (binding.usernameLayout != null) binding.usernameLayout.setError(null);
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            if (binding.passwordLayout != null) binding.passwordLayout.setError("Password is required");
            isValid = false;
        } else if (!Validator.isValidPassword(password)) {
            if (binding.passwordLayout != null) binding.passwordLayout.setError("Password must be at least 8 characters long");
            isValid = false;
        } else {
            if (binding.passwordLayout != null) binding.passwordLayout.setError(null);
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            if (binding.confirmPasswordLayout != null) binding.confirmPasswordLayout.setError("Please confirm your password");
            isValid = false;
        } else if (!Validator.doPasswordsMatch(password, confirmPassword)) {
            if (binding.confirmPasswordLayout != null) binding.confirmPasswordLayout.setError("Passwords do not match");
            isValid = false;
        } else {
            if (binding.confirmPasswordLayout != null) binding.confirmPasswordLayout.setError(null);
        }

        // Validate checkboxes
        if (!isCheckboxChecked(binding.cbTerms)) {
            showErrorDialog("You must agree to the Terms and Conditions to continue");
            isValid = false;
        }

        if (!isCheckboxChecked(binding.cbNotRobot)) {
            showErrorDialog("Please confirm you're not a robot");
            isValid = false;
        }

        return isValid;
    }

    private boolean isCheckboxChecked(android.widget.CheckBox checkBox) {
        return checkBox != null && checkBox.isChecked();
    }

    private void createFirebaseUser(String email, String password, String fullName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser fbUser = mAuth.getCurrentUser();
                        if (fbUser != null) {
                            handleSuccessfulSignup(fbUser, fullName, email);
                        } else {
                            showLoading(false);
                            if (binding.subtitle != null) {
                                binding.subtitle.setText("Create an account to make a difference");
                            }
                            showErrorDialog("Failed to create user account");
                        }
                    } else {
                        handleSignupError(task.getException());
                    }
                });
    }

    private void handleSuccessfulSignup(FirebaseUser fbUser, String fullName, String email) {
        Log.d(TAG, "Firebase user created successfully: " + fbUser.getUid());

        // Update user profile with display name
        updateUserProfile(fbUser, fullName)
                .addOnCompleteListener(profileTask -> {
                    if (profileTask.isSuccessful()) {
                        saveUserToFirestore(fbUser, fullName, email);
                    } else {
                        handleSignupError(profileTask.getException());
                    }
                });
    }

    private com.google.android.gms.tasks.Task<Void> updateUserProfile(FirebaseUser fbUser, String fullName) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .build();

        return fbUser.updateProfile(profileUpdates);
    }

    private void saveUserToFirestore(FirebaseUser fbUser, String fullName, String email) {
        User appUser = new User(fullName, email, null, fbUser.getUid());

        db.collection("users")
                .document(fbUser.getUid())
                .set(appUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User data saved to Firestore");
                    sendVerificationEmail(fbUser, appUser);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save user to Firestore: " + e.getMessage());
                    sendVerificationEmail(fbUser, appUser);
                });
    }

    private void sendVerificationEmail(FirebaseUser fbUser, User appUser) {
        fbUser.sendEmailVerification()
                .addOnCompleteListener(emailTask -> {
                    showLoading(false);
                    if (binding.subtitle != null) {
                        binding.subtitle.setText("Create an account to make a difference");
                    }

                    if (emailTask.isSuccessful()) {
                        handleVerificationEmailSent(fbUser, appUser);
                    } else {
                        handleVerificationEmailFailed(emailTask.getException());
                    }
                });
    }

    private void handleVerificationEmailSent(FirebaseUser fbUser, User appUser) {
        sessionManager.createLoginSession(appUser);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Account Created Successfully!")
                .setMessage("Your account has been created! We've sent a verification email to your email address. Please verify your email before logging in.")
                .setPositiveButton("OK", (dialog, which) -> {
                    navigationHelper.navigateToLogin();
                    Toast.makeText(this, "Please check your email for verification", Toast.LENGTH_LONG).show();
                })
                .setCancelable(false)
                .show();

        Log.d(TAG, "Verification email sent to: " + fbUser.getEmail());
    }

    private void handleVerificationEmailFailed(Exception exception) {
        showLoading(false);
        if (binding.subtitle != null) {
            binding.subtitle.setText("Create an account to make a difference");
        }
        String errorMessage = "Failed to send verification email. Please try logging in and request a new verification email.";
        if (exception != null) {
            errorMessage += "\nError: " + exception.getMessage();
        }

        showErrorDialog(errorMessage);
        Log.e(TAG, "Verification email failed: " + (exception != null ? exception.getMessage() : "Unknown error"));
    }

    private void handleSignupError(Exception exception) {
        showLoading(false);
        if (binding.subtitle != null) {
            binding.subtitle.setText("Create an account to make a difference");
        }
        String errorMessage = "Sign-up failed. Please try again.";

        if (exception != null) {
            errorMessage = getFirebaseAuthErrorMessage(exception);
            Log.e(TAG, "Signup error: " + exception.getMessage());
        }

        showErrorDialog(errorMessage);
    }

    private String getFirebaseAuthErrorMessage(Exception exception) {
        String errorMessage = exception.getMessage();

        if (errorMessage == null) {
            return "An unknown error occurred. Please try again.";
        }

        if (errorMessage.contains("email address is already in use")) {
            return "This email address is already registered. Please use a different email or try logging in.";
        } else if (errorMessage.contains("password is invalid")) {
            return "The password is too weak. Please use a stronger password.";
        } else if (errorMessage.contains("network error")) {
            return "Network error. Please check your internet connection and try again.";
        } else if (errorMessage.contains("invalid email")) {
            return "The email address is invalid. Please enter a valid email address.";
        } else {
            return "Sign-up failed: " + errorMessage;
        }
    }

    private void showErrorDialog(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLoading(boolean show) {
        if (binding.btnSignup != null) {
            binding.btnSignup.setEnabled(!show);
            binding.btnSignup.setText(show ? "Creating Account..." : "Sign Up");
        }
        if (binding.tvLoginLink != null) {
            binding.tvLoginLink.setEnabled(!show);
        }
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FULL_NAME, safelyGetText(binding.etFullName));
        outState.putString(KEY_EMAIL, safelyGetText(binding.etUsername));
        outState.putString(KEY_PASSWORD, safelyGetText(binding.etPassword));
        outState.putString(KEY_CONFIRM_PASSWORD, safelyGetText(binding.etConfirmPassword));
        outState.putBoolean("terms", isCheckboxChecked(binding.cbTerms));
        outState.putBoolean("not_robot", isCheckboxChecked(binding.cbNotRobot));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}