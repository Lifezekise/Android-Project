package com.athisintiya.helpinghands.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.databinding.ActivityMainBinding;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavigationHelper navigationHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize helpers
        navigationHelper = new NavigationHelper(this);
        sessionManager = new SessionManager(this);

        // Setup all components
        setupToolbar();
        setupClickListeners();
        updateWelcomeMessage();
        setupNavigationDrawer();

        // Check if user is logged in
        checkAuthentication();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);

        // Enable the navigation icon (hamburger menu)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        binding.toolbar.setNavigationOnClickListener(v -> {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setupClickListeners() {
        // Main action buttons
        binding.findHelpButton.setOnClickListener(v -> {
            navigationHelper.navigateToResources();
        });

        binding.donateButton.setOnClickListener(v -> {
            navigationHelper.navigateToDonate();
        });

        binding.volunteerButton.setOnClickListener(v -> {
            navigationHelper.navigateToVolunteer();
        });

        binding.accountButton.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                navigationHelper.navigateToAccount();
            } else {
                Toast.makeText(this, "Please log in to access your account", Toast.LENGTH_SHORT).show();
                navigationHelper.navigateToLogin();
            }
        });

        binding.cardReport.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                navigationHelper.navigateToReport();
            } else {
                Toast.makeText(this, "Please log in to report an issue", Toast.LENGTH_SHORT).show();
                navigationHelper.navigateToLogin();
            }
        });
    }

    private void setupNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                // Handle navigation menu item clicks based on your new menu structure
                if (itemId == R.id.nav_home) {
                    // Already on home/dashboard, just close drawer
                    // You could refresh the dashboard here if needed
                    Toast.makeText(MainActivity.this, "Dashboard", Toast.LENGTH_SHORT).show();
                }
                else if (itemId == R.id.nav_map) {
                    // Map View - You might need to create this activity
                    Toast.makeText(MainActivity.this, "Map View - Coming Soon", Toast.LENGTH_SHORT).show();
                    // navigationHelper.navigateToMap(); // If you create this method
                }
                else if (itemId == R.id.nav_report) {
                    // Report Individual
                    if (sessionManager.isLoggedIn()) {
                        navigationHelper.navigateToReport();
                    } else {
                        Toast.makeText(MainActivity.this, "Please log in to report an individual", Toast.LENGTH_SHORT).show();
                        navigationHelper.navigateToLogin();
                    }
                }
                else if (itemId == R.id.nav_resources) {
                    // Find Resources
                    navigationHelper.navigateToResources();
                }
                else if (itemId == R.id.nav_donate) {
                    // Make Donation
                    navigationHelper.navigateToDonate();
                }
                else if (itemId == R.id.nav_volunteer) {
                    // Volunteer
                    navigationHelper.navigateToVolunteer();
                }
                else if (itemId == R.id.nav_profile) {
                    // My Profile (under Account submenu)
                    if (sessionManager.isLoggedIn()) {
                        navigationHelper.navigateToAccount();
                    } else {
                        Toast.makeText(MainActivity.this, "Please log in to view profile", Toast.LENGTH_SHORT).show();
                        navigationHelper.navigateToLogin();
                    }
                }
                else if (itemId == R.id.nav_settings) {
                    // Settings (under Account submenu)
                    Toast.makeText(MainActivity.this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
                    // navigationHelper.navigateToSettings(); // If you create this method
                }
                else if (itemId == R.id.nav_notifications) {
                    // Notifications (under Account submenu)
                    Toast.makeText(MainActivity.this, "Notifications - Coming Soon", Toast.LENGTH_SHORT).show();
                    // navigationHelper.navigateToNotifications(); // If you create this method
                }
                else if (itemId == R.id.nav_help) {
                    // Help & Support (under Support submenu)
                    showHelpAndSupport();
                }
                else if (itemId == R.id.nav_about) {
                    // About (under Support submenu)
                    showAboutDialog();
                }
                else if (itemId == R.id.nav_logout) {
                    // Logout (main menu item)
                    logoutUser();
                }
                else {
                    // Handle unknown menu items
                    Toast.makeText(MainActivity.this, "Feature coming soon", Toast.LENGTH_SHORT).show();
                }

                // Close the drawer after selection
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Update navigation header with user info if logged in
        updateNavigationHeader();
    }

    private void updateNavigationHeader() {
        // Update the navigation header with user information
        if (sessionManager.isLoggedIn()) {
            String userName = sessionManager.getUserName();
            String userEmail = sessionManager.getEmail();

            
            View headerView = binding.navigationView.getHeaderView(0);
            TextView tvUserName = headerView.findViewById(R.id.tvUserName);
            TextView tvUserEmail = headerView.findViewById(R.id.tvUserEmail);

            if (tvUserName != null && userName != null && !userName.isEmpty()) {
                tvUserName.setText(userName);
            }
            if (tvUserEmail != null && userEmail != null && !userEmail.isEmpty()) {
                tvUserEmail.setText(userEmail);
            }
           
        }
    }

    private void updateWelcomeMessage() {
        if (sessionManager.isLoggedIn()) {
            String userName = sessionManager.getUserName();
            if (userName != null && !userName.isEmpty()) {
                binding.tvWelcomeMessage.setText("Welcome back, " + userName + "!");
            } else {
                binding.tvWelcomeMessage.setText("Welcome to Helping Hands!");
            }
        } else {
            binding.tvWelcomeMessage.setText("Community support and resources");
        }
    }

    private void checkAuthentication() {
         if (!sessionManager.isLoggedIn()) {
             navigationHelper.navigateToLogin();
         }
    }

    private void showHelpAndSupport() {
        Toast.makeText(this, "Help & Support - Contact us at support@helpinghands.org", Toast.LENGTH_LONG).show();

        
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:support@helpinghands.org"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Helping Hands Support");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send support email..."));
        } catch (Exception e) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
        
    }

    private void showAboutDialog() {
        // Show about information
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("About Helping Hands")
                .setMessage("Helping Hands v1.0\n\nA community support app connecting people in need with resources, shelters, and volunteers.\n\nBuilt with ❤️ for the community.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void makeEmergencyCall() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:10111")); // South Africa emergency number
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to make emergency call", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void logoutUser() {
        if (sessionManager.isLoggedIn()) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sessionManager.logoutUser();
                        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        navigationHelper.navigateToLogin();
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data when returning to main activity
        updateWelcomeMessage();
        updateNavigationHeader();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Close drawer if open when activity goes to background
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up binding to prevent memory leaks
        binding = null;
    }
}
