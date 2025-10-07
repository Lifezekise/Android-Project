package com.athisintiya.helpinghands.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.model.User;
import com.athisintiya.helpinghands.repository.UserRepository;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;

/**
 * AccountActivity displays user account details and provides navigation back to the main dashboard.
 */
public class AccountActivity extends AppCompatActivity {
    private TextView tvFullName, tvEmail, tvDonationCount;
    private Button btnBack, btnLogout;
    private NavigationHelper navigationHelper;
    private UserRepository userRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        navigationHelper = new NavigationHelper(this);
        userRepository = UserRepository.getInstance(this);
        sessionManager = new SessionManager(this);

        // Initialize views
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvUsername); // Note: ID remains tvUsername for compatibility
        tvDonationCount = findViewById(R.id.tvDonationCount);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        // Fix text colors without needing specific IDs
        fixTextColors();

        // Load and display user data
        User currentUser = userRepository.getCurrentUser();
        if (currentUser != null) {
            tvFullName.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "N/A");
            tvEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A");
            tvDonationCount.setText(String.format("Donations: %d", currentUser.getDonationCount()));
        } else {
            tvFullName.setText("No user logged in");
            tvEmail.setText("N/A");
            tvDonationCount.setText("Donations: 0");
        }

        // Set click listeners
        btnBack.setOnClickListener(v -> navigationHelper.navigateToMain());
        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            navigationHelper.navigateToLogin();
        });
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
            if (child instanceof ViewGroup) {
                changeTextColorsRecursive((ViewGroup) child);
            }
        }
    }
}