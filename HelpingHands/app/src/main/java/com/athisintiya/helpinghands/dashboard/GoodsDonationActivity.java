package com.athisintiya.helpinghands.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.databinding.ActivityGoodsDonationBinding;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GoodsDonationActivity extends AppCompatActivity {
    private ActivityGoodsDonationBinding binding;
    private NavigationHelper navigationHelper;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String KEY_GOODS_TYPE = "goods_type";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_IS_NEW = "is_new";
    private static final String KEY_DESCRIPTION = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoodsDonationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationHelper = new NavigationHelper(this);
        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set up goods type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.goods_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spGoodsType.setAdapter(adapter);

        // Set up button listeners
        binding.btnSubmit.setOnClickListener(v -> submitDonation());
        binding.btnBack.setOnClickListener(v -> navigationHelper.navigateToMain());

        // Restore state
        if (savedInstanceState != null) {
            binding.spGoodsType.setSelection(savedInstanceState.getInt(KEY_GOODS_TYPE));
            binding.etQuantity.setText(savedInstanceState.getString(KEY_QUANTITY));
            binding.cbIsNew.setChecked(savedInstanceState.getBoolean(KEY_IS_NEW));
            binding.etDescription.setText(savedInstanceState.getString(KEY_DESCRIPTION));
        }
    }

    private void submitDonation() {
        String goodsType = binding.spGoodsType.getSelectedItem() != null ? binding.spGoodsType.getSelectedItem().toString() : "";
        String quantity = binding.etQuantity.getText().toString().trim();
        boolean isNew = binding.cbIsNew.isChecked();
        String description = binding.etDescription.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(goodsType) || goodsType.equals("Clothing")) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Please select a goods type")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(quantity)) {
            binding.quantityLayout.setError("Quantity is required");
            return;
        }

        try {
            int qty = Integer.parseInt(quantity);
            if (qty <= 0) {
                binding.quantityLayout.setError("Please enter a positive quantity");
                return;
            }

            com.google.firebase.auth.FirebaseUser fbUser = mAuth.getCurrentUser();
            if (fbUser != null) {
                // Save donation to Firestore
                Map<String, Object> donation = new HashMap<>();
                donation.put("userId", fbUser.getUid());
                donation.put("goodsType", goodsType);
                donation.put("quantity", qty);
                donation.put("isNew", isNew);
                donation.put("description", description);
                donation.put("type", "goods");
                donation.put("timestamp", System.currentTimeMillis());
                db.collection("donations").add(donation);

                // Increment donation count
                db.collection("users").document(fbUser.getUid())
                        .update("donationCount", com.google.firebase.firestore.FieldValue.increment(1));

                new MaterialAlertDialogBuilder(this)
                        .setTitle("Success")
                        .setMessage("Goods donation submitted successfully")
                        .setPositiveButton("OK", (dialog, which) -> navigationHelper.navigateToMain())
                        .show();
            } else {
                new MaterialAlertDialogBuilder(this)
                        .setMessage("User not logged in")
                        .setPositiveButton("OK", null)
                        .show();
            }
        } catch (NumberFormatException e) {
            binding.quantityLayout.setError("Please enter a valid number");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_GOODS_TYPE, binding.spGoodsType.getSelectedItemPosition());
        outState.putString(KEY_QUANTITY, binding.etQuantity.getText().toString());
        outState.putBoolean(KEY_IS_NEW, binding.cbIsNew.isChecked());
        outState.putString(KEY_DESCRIPTION, binding.etDescription.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}