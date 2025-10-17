package com.athisintiya.helpinghands.dashboard;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.athisintiya.helpinghands.databinding.ActivityMoneyDonationBinding;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MoneyDonationActivity extends AppCompatActivity {
    private ActivityMoneyDonationBinding binding;
    private NavigationHelper navigationHelper;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String KEY_AMOUNT = "amount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoneyDonationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationHelper = new NavigationHelper(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.submitDonationBtn.setOnClickListener(v -> handleMoneyDonation());
        binding.backButton.setOnClickListener(v -> navigationHelper.navigateToDonate());

        if (savedInstanceState != null) {
            binding.amountField.setText(savedInstanceState.getString(KEY_AMOUNT));
        }
    }

    private void handleMoneyDonation() {
        String amountStr = binding.amountField.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            binding.amountField.setError("Required");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                binding.amountField.setError("Positive amount required");
                return;
            }

            com.google.firebase.auth.FirebaseUser fbUser = mAuth.getCurrentUser();
            if (fbUser != null) {
                Map<String, Object> donation = new HashMap<>();
                donation.put("userId", fbUser.getUid());
                donation.put("amount", amount);
                donation.put("type", "money");
                donation.put("timestamp", System.currentTimeMillis());
                db.collection("donations").add(donation);

                db.collection("users").document(fbUser.getUid())
                        .update("donationCount", com.google.firebase.firestore.FieldValue.increment(1));

                new MaterialAlertDialogBuilder(this)
                        .setMessage("Donated R" + amount)
                        .setPositiveButton("OK", (dialog, which) -> navigationHelper.navigateToMain())
                        .show();
            } else {
                new MaterialAlertDialogBuilder(this)
                        .setMessage("User not logged in")
                        .setPositiveButton("OK", null)
                        .show();
            }
        } catch (NumberFormatException e) {
            binding.amountField.setError("Valid number required");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_AMOUNT, binding.amountField.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}