package com.athisintiya.helpinghands.auth;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

public class SignupActivity extends AppCompatActivity {
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private CheckBox cbTerms, cbNotRobot;
    private Button btnSignup;
    private TextView tvLoginLink;
    private NavigationHelper navigationHelper;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_CONFIRM_PASSWORD = "confirm_password";
    private static final String KEY_TERMS = "terms";
    private static final String KEY_NOT_ROBOT = "not_robot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        navigationHelper = new NavigationHelper(this);
        userRepository = UserRepository.getInstance(this);
        sessionManager = new SessionManager(this);

        // Fix text colors without needing IDs
        fixTextColors();

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        cbNotRobot = findViewById(R.id.cbNotRobot);
        btnSignup = findViewById(R.id.btnSignup);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Restore state if available
        if (savedInstanceState != null) {
            etFullName.setText(savedInstanceState.getString(KEY_FULL_NAME, ""));
            etEmail.setText(savedInstanceState.getString(KEY_EMAIL, ""));
            etPassword.setText(savedInstanceState.getString(KEY_PASSWORD, ""));
            etConfirmPassword.setText(savedInstanceState.getString(KEY_CONFIRM_PASSWORD, ""));
            cbTerms.setChecked(savedInstanceState.getBoolean(KEY_TERMS, false));
            cbNotRobot.setChecked(savedInstanceState.getBoolean(KEY_NOT_ROBOT, false));
        }

        // Set click listeners
        btnSignup.setOnClickListener(v -> handleSignup());
        tvLoginLink.setOnClickListener(v -> navigationHelper.navigateToLogin());
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
        outState.putString(KEY_FULL_NAME, etFullName.getText().toString());
        outState.putString(KEY_EMAIL, etEmail.getText().toString());
        outState.putString(KEY_PASSWORD, etPassword.getText().toString());
        outState.putString(KEY_CONFIRM_PASSWORD, etConfirmPassword.getText().toString());
        outState.putBoolean(KEY_TERMS, cbTerms.isChecked());
        outState.putBoolean(KEY_NOT_ROBOT, cbNotRobot.isChecked());
    }

    private void handleSignup() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (Validator.isFieldEmpty(fullName)) {
            etFullName.setError("Please enter full name");
            return;
        }

        if (Validator.isFieldTooLong(fullName)) {
            etFullName.setError("Full name is too long");
            return;
        }

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

        if (userRepository.getUserByEmail(email) != null) {
            etEmail.setError("Email already registered");
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

        if (Validator.isFieldEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm password");
            return;
        }

        if (!Validator.doPasswordsMatch(password, confirmPassword)) {
            etConfirmPassword.setError("Passwords don't match");
            return;
        }

        if (!cbTerms.isChecked()) {
            showError("You must agree to the Terms and Conditions");
            return;
        }

        if (!cbNotRobot.isChecked()) {
            showError("Please confirm you're not a robot");
            return;
        }

        // Register user
        User newUser = new User(fullName, email, password, email);
        userRepository.registerUser(newUser);
        sessionManager.createLoginSession(newUser);
        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
        navigationHelper.navigateToMain();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}