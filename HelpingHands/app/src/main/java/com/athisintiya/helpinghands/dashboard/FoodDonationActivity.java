package com.athisintiya.helpinghands.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.databinding.ActivityFoodDonationBinding;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FoodDonationActivity extends AppCompatActivity {
    private ActivityFoodDonationBinding binding;
    private NavigationHelper navigationHelper;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String KEY_FOOD_TYPE = "food_type";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_PERISHABLE = "perishable";
    private static final String KEY_INSTRUCTIONS = "instructions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodDonationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationHelper = new NavigationHelper(this);
        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.food_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spFoodType.setAdapter(adapter);

        binding.btnSubmit.setOnClickListener(v -> submitDonation());
        binding.btnBack.setOnClickListener(v -> navigationHelper.navigateToMain());

        if (savedInstanceState != null) {
            binding.spFoodType.setSelection(savedInstanceState.getInt(KEY_FOOD_TYPE));
            binding.etQuantity.setText(savedInstanceState.getString(KEY_QUANTITY));
            binding.cbPerishable.setChecked(savedInstanceState.getBoolean(KEY_PERISHABLE));
            binding.etSpecialInstructions.setText(savedInstanceState.getString(KEY_INSTRUCTIONS));
        }
    }

    private void submitDonation() {
        String foodType = binding.spFoodType.getSelectedItem() != null ? binding.spFoodType.getSelectedItem().toString() : "";
        String quantity = binding.etQuantity.getText().toString().trim();
        boolean isPerishable = binding.cbPerishable.isChecked();
        String instructions = binding.etSpecialInstructions.getText().toString().trim();

        if (TextUtils.isEmpty(foodType) || foodType.equals("Fruits and Vegetables")) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Select food type")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(quantity)) {
            binding.etQuantity.setError("Required");
            return;
        }

        try {
            int qty = Integer.parseInt(quantity);
            if (qty <= 0) {
                binding.etQuantity.setError("Positive quantity required");
                return;
            }

            com.google.firebase.auth.FirebaseUser fbUser = mAuth.getCurrentUser();
            if (fbUser != null) {
                Map<String, Object> donation = new HashMap<>();
                donation.put("userId", fbUser.getUid());
                donation.put("foodType", foodType);
                donation.put("quantity", qty);
                donation.put("perishable", isPerishable);
                donation.put("instructions", instructions);
                donation.put("type", "food");
                donation.put("timestamp", System.currentTimeMillis());
                db.collection("donations").add(donation);

                db.collection("users").document(fbUser.getUid())
                        .update("donationCount", com.google.firebase.firestore.FieldValue.increment(1));

                new MaterialAlertDialogBuilder(this)
                        .setMessage("Food donation submitted")
                        .setPositiveButton("OK", (dialog, which) -> navigationHelper.navigateToMain())
                        .show();
            } else {
                new MaterialAlertDialogBuilder(this)
                        .setMessage("User not logged in")
                        .setPositiveButton("OK", null)
                        .show();
            }
        } catch (NumberFormatException e) {
            binding.etQuantity.setError("Valid number required");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_FOOD_TYPE, binding.spFoodType.getSelectedItemPosition());
        outState.putString(KEY_QUANTITY, binding.etQuantity.getText().toString());
        outState.putBoolean(KEY_PERISHABLE, binding.cbPerishable.isChecked());
        outState.putString(KEY_INSTRUCTIONS, binding.etSpecialInstructions.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}