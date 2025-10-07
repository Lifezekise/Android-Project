package com.athisintiya.helpinghands.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.repository.UserRepository;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;

public class FoodDonationActivity extends AppCompatActivity {
    private Spinner foodTypeSpinner;
    private EditText etQuantity, etSpecialInstructions;
    private CheckBox cbPerishable;
    private Button btnSubmit, btnBack;
    private NavigationHelper navigationHelper;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private static final String KEY_FOOD_TYPE = "food_type";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_PERISHABLE = "perishable";
    private static final String KEY_INSTRUCTIONS = "instructions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_donation);

        navigationHelper = new NavigationHelper(this);
        userRepository = UserRepository.getInstance(this);
        sessionManager = new SessionManager(this);

        fixTextColors();

        foodTypeSpinner = findViewById(R.id.spFoodType);
        etQuantity = findViewById(R.id.etQuantity);
        cbPerishable = findViewById(R.id.cbPerishable);
        etSpecialInstructions = findViewById(R.id.etSpecialInstructions);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        // Set up food type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.food_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodTypeSpinner.setAdapter(adapter);

        if (savedInstanceState != null) {
            foodTypeSpinner.setSelection(savedInstanceState.getInt(KEY_FOOD_TYPE, 0));
            etQuantity.setText(savedInstanceState.getString(KEY_QUANTITY, ""));
            cbPerishable.setChecked(savedInstanceState.getBoolean(KEY_PERISHABLE, false));
            etSpecialInstructions.setText(savedInstanceState.getString(KEY_INSTRUCTIONS, ""));
        }

        btnSubmit.setOnClickListener(v -> submitDonation());
        btnBack.setOnClickListener(v -> navigationHelper.navigateToMain());
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
        outState.putInt(KEY_FOOD_TYPE, foodTypeSpinner.getSelectedItemPosition());
        outState.putString(KEY_QUANTITY, etQuantity.getText().toString());
        outState.putBoolean(KEY_PERISHABLE, cbPerishable.isChecked());
        outState.putString(KEY_INSTRUCTIONS, etSpecialInstructions.getText().toString());
    }

    private void submitDonation() {
        String foodType = foodTypeSpinner.getSelectedItem() != null ? foodTypeSpinner.getSelectedItem().toString() : "";
        String quantity = etQuantity.getText().toString().trim();
        boolean isPerishable = cbPerishable.isChecked();
        String instructions = etSpecialInstructions.getText().toString().trim();

        if (TextUtils.isEmpty(foodType) || foodType.equals("Fruits and Vegetables")) {
            Toast.makeText(this, "Please select a food type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(quantity)) {
            etQuantity.setError("Please enter quantity");
            return;
        }

        try {
            int qty = Integer.parseInt(quantity);
            if (qty <= 0) {
                etQuantity.setError("Please enter a positive quantity");
                return;
            }

            String email = sessionManager.getUserEmail();
            if (email != null) {
                userRepository.recordDonation(email);
                Toast.makeText(this, "Food donation submitted successfully", Toast.LENGTH_SHORT).show();
                navigationHelper.navigateToMain();
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Please enter a valid number");
        }
    }
}