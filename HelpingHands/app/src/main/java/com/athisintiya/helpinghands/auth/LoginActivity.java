package com.athisintiya.helpinghands.auth;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.model.User;
import com.athisintiya.helpinghands.repository.UserRepository;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;
import com.athisintiya.helpinghands.utils.Validator;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignupLink;
    private NavigationHelper navigationHelper;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    public static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        navigationHelper = new NavigationHelper(this);
        userRepository = UserRepository.getInstance(this);
        sessionManager = new SessionManager(this);

        // Fix text colors without needing IDs
        fixTextColors();

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotUsername = findViewById(R.id.tvForgotUsername);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignupLink = findViewById(R.id.tvSignupLink);

        // Restore state if available
        if (savedInstanceState != null) {
            etEmail.setText(savedInstanceState.getString(KEY_EMAIL, ""));
            etPassword.setText(savedInstanceState.getString(KEY_PASSWORD, ""));
        }

        // Set click listeners
        btnLogin.setOnClickListener(v -> handleLogin());
        tvSignupLink.setOnClickListener(v -> navigationHelper.navigateToSignup());
        tvForgotUsername.setOnClickListener(v -> showForgotUsernameDialog());
        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void fixTextColors() {
        ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
        changeTextColorsRecursive(rootView);
    }

    private void changeTextColorsRecursive(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            if (child instanceof TextView) {
                ((TextView) child).setTextColor(Color.BLACK);
            }
            if (child instanceof EditText) {
                ((EditText) child).setTextColor(Color.BLACK);
                ((EditText) child).setHintTextColor(Color.GRAY);
            }
            if (child instanceof ViewGroup) {
                changeTextColorsRecursive((ViewGroup) child);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_EMAIL, etEmail.getText().toString());
        outState.putString(KEY_PASSWORD, etPassword.getText().toString());
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (Validator.isFieldEmpty(email)) {
            etEmail.setError("Please enter email");
            return;
        }

        if (Validator.isFieldTooLong(email)) {
            etEmail.setError("Email is too long");
            return;
        }

        if (!Validator.isValidEmail(email)) {
            etEmail.setError("Please enter a valid email");
            return;
        }

        if (Validator.isFieldEmpty(password)) {
            etPassword.setError("Please enter password");
            return;
        }

        if (Validator.isFieldTooLong(password)) {
            etPassword.setError("Password is too long");
            return;
        }

        if (!Validator.isValidPassword(password)) {
            etPassword.setError("Password must be at least 8 characters");
            return;
        }

        // Authenticate user
        if (userRepository.authenticate(email, password)) {
            User user = userRepository.getUserByEmail(email);
            sessionManager.createLoginSession(user);
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            navigationHelper.navigateToMain();
        } else {
            showError("Invalid email or password");
        }
    }

    private void showForgotUsernameDialog() {
        Toast.makeText(this, "Forgot username functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showForgotPasswordDialog() {
        Toast.makeText(this, "Forgot password functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}