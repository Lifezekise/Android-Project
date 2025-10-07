package com.athisintiya.helpinghands.dashboard;

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
import com.athisintiya.helpinghands.repository.UserRepository;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;

public class DonateActivity extends AppCompatActivity {
    private EditText amountField;
    private NavigationHelper navigationHelper;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private static final String KEY_AMOUNT = "amount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        navigationHelper = new NavigationHelper(this);
        userRepository = UserRepository.getInstance(this);
        sessionManager = new SessionManager(this);

        // Fix text colors without needing IDs
        fixTextColors();

        // Initialize views
        amountField = findViewById(R.id.amountField);
        Button moneyBtn = findViewById(R.id.moneyBtn);
        Button foodButton = findViewById(R.id.foodButton);
        Button goodsButton = findViewById(R.id.goodsButton);
        Button backButton = findViewById(R.id.backButton);

        // Restore state if available
        if (savedInstanceState != null) {
            amountField.setText(savedInstanceState.getString(KEY_AMOUNT, ""));
        }

        // Set click listeners
        moneyBtn.setOnClickListener(v -> handleMoneyDonation(amountField.getText().toString().trim()));
        foodButton.setOnClickListener(v -> navigationHelper.navigateToFoodDonation());
        goodsButton.setOnClickListener(v -> navigationHelper.navigateToGoodsDonation());
        backButton.setOnClickListener(v -> navigationHelper.navigateToMain());
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
        outState.putString(KEY_AMOUNT, amountField.getText().toString());
    }

    private void handleMoneyDonation(String amount) {
        if (TextUtils.isEmpty(amount)) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double donation = Double.parseDouble(amount);
            if (donation > 0) {
                String email = sessionManager.getUserEmail();
                if (email != null) {
                    userRepository.recordDonation(email);
                }
                Toast.makeText(this, "Thank you for your donation of R" + String.format("%.2f", donation), Toast.LENGTH_LONG).show();
                navigationHelper.navigateToMain();
            } else {
                Toast.makeText(this, "Please enter a positive amount", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }
}