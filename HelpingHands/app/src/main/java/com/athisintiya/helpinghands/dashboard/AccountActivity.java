package com.athisintiya.helpinghands.dashboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.databinding.ActivityAccountBinding;
import com.athisintiya.helpinghands.model.User;
import com.athisintiya.helpinghands.repository.UserRepository;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;
import com.athisintiya.helpinghands.utils.Validator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AccountActivity extends AppCompatActivity {
    private ActivityAccountBinding binding;
    private NavigationHelper navigationHelper;
    private SessionManager sessionManager;
    private UserRepository userRepository;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationHelper = new NavigationHelper(this);
        sessionManager = new SessionManager(this);
        userRepository = UserRepository.getInstance(this);

        // Check if user is logged in
        currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            navigationHelper.navigateToLogin();
            finish();
            return;
        }

        setupViews();
        loadUserData();
    }

    private void setupViews() {
        binding.btnUpdate.setOnClickListener(v -> updateProfile());
        binding.btnBack.setOnClickListener(v -> navigationHelper.navigateToMain());
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void loadUserData() {
        currentUser = userRepository.getCurrentUser();
        if (currentUser != null) {
            updateUIWithUserData(currentUser);
        } else {
            showLoginRequiredDialog();
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void updateUIWithUserData(User user) {
        // Set all user data including phone and address
        binding.etFullName.setText(user.getFullName());
        binding.etUsername.setText(user.getEmail());
        binding.donationCount.setText(getString(R.string.donation_count, user.getDonationCount()));

        // Update profile completion status
        updateProfileCompletionStatus(user);
    }

    private void updateProfileCompletionStatus(User user) {
        boolean isComplete = !TextUtils.isEmpty(user.getFullName()) &&
                !TextUtils.isEmpty(user.getEmail()) &&
                !TextUtils.isEmpty(user.getPhoneNumber()) &&
                !TextUtils.isEmpty(user.getAddress());

        // You can add a status TextView in your layout to show this
        if (isComplete) {
            // Profile is complete
            Toast.makeText(this, "Profile is complete!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile() {
        String name = binding.etFullName.getText().toString().trim();
        String email = binding.etUsername.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            binding.etFullName.setError("Full name is required");
            binding.etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            binding.etUsername.setError("Email is required");
            binding.etUsername.requestFocus();
            return;
        }

        if (!Validator.isValidEmail(email)) {
            binding.etUsername.setError("Invalid email format");
            binding.etUsername.requestFocus();
            return;
        }


        if (currentUser != null) {
            // Create updated user object with ALL fields
            User updatedUser = new User();
            updatedUser.setId(currentUser.getId());
            updatedUser.setFullName(name);
            updatedUser.setEmail(email);
            updatedUser.setDonationCount(currentUser.getDonationCount());
            updatedUser.setProfileImageUrl(currentUser.getProfileImageUrl());

            // Show loading state
            binding.btnUpdate.setEnabled(false);
            binding.btnUpdate.setText("Updating...");

            // Update user via repository
            userRepository.updateUser(updatedUser, new UserRepository.UserUpdateCallback() {
                @Override
                public void onSuccess() {
                    // Update successful
                    currentUser = updatedUser;
                    sessionManager.updateUserDetails(updatedUser);

                    // Restore button state
                    binding.btnUpdate.setEnabled(true);
                    binding.btnUpdate.setText("Update Profile");

                    showSuccessMessage("Profile updated successfully!");
                    updateProfileCompletionStatus(updatedUser);
                }

                @Override
                public void onFailure(String error) {
                    // Update failed
                    binding.btnUpdate.setEnabled(true);
                    binding.btnUpdate.setText("Update Profile");
                    showErrorMessage("Failed to update profile: " + error);
                }
            });
        } else {
            showLoginRequiredDialog();
        }
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.logoutUser();
                    navigationHelper.navigateToLogin();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSuccessMessage(String message) {
        new MaterialAlertDialogBuilder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showErrorMessage(String message) {
        new MaterialAlertDialogBuilder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLoginRequiredDialog() {
        new MaterialAlertDialogBuilder(this)
                .setMessage("User not logged in. Please login again.")
                .setPositiveButton("OK", (dialog, which) -> {
                    navigationHelper.navigateToLogin();
                    finish();
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadUserData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}