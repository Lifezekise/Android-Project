package com.athisintiya.helpinghands.dashboard;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    private Button btnFindHelp, btnDonate, btnVolunteer, btnAccount;
    private NavigationHelper navigationHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationHelper = new NavigationHelper(this);
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            navigationHelper.navigateToLogin();
            finish();
            return;
        }

        // Initialize views
        btnFindHelp = findViewById(R.id.find_help_button);
        btnDonate = findViewById(R.id.donate_button);
        btnVolunteer = findViewById(R.id.volunteer_button);
        btnAccount = findViewById(R.id.account_button);

        // Set click listeners
        btnFindHelp.setOnClickListener(v -> navigationHelper.navigateToResources());
        btnDonate.setOnClickListener(v -> navigationHelper.navigateToDonate());
        btnVolunteer.setOnClickListener(v -> navigationHelper.navigateToVolunteer());
        btnAccount.setOnClickListener(v -> navigationHelper.navigateToAccount());
    }
}