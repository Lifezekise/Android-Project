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

public class GoodsDonationActivity extends AppCompatActivity {
    private Spinner goodsTypeSpinner;
    private EditText etQuantity, etDescription;
    private CheckBox cbIsNew;
    private Button btnSubmit, btnBack;
    private NavigationHelper navigationHelper;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private static final String KEY_GOODS_TYPE = "goods_type";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_IS_NEW = "is_new";
    private static final String KEY_DESCRIPTION = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_donation);

        navigationHelper = new NavigationHelper(this);
        userRepository = UserRepository.getInstance(this);
        sessionManager = new SessionManager(this);

        // Fix text colors without needing IDs
        fixTextColors();

        // Initialize views
        goodsTypeSpinner = findViewById(R.id.spGoodsType);
        etQuantity = findViewById(R.id.etQuantity);
        cbIsNew = findViewById(R.id.cbIsNew);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        // Set up goods type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.goods_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goodsTypeSpinner.setAdapter(adapter);

        // Restore state if available
        if (savedInstanceState != null) {
            goodsTypeSpinner.setSelection(savedInstanceState.getInt(KEY_GOODS_TYPE, 0));
            etQuantity.setText(savedInstanceState.getString(KEY_QUANTITY, ""));
            cbIsNew.setChecked(savedInstanceState.getBoolean(KEY_IS_NEW, false));
            etDescription.setText(savedInstanceState.getString(KEY_DESCRIPTION, ""));
        }

        // Set click listeners
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
        outState.putInt(KEY_GOODS_TYPE, goodsTypeSpinner.getSelectedItemPosition());
        outState.putString(KEY_QUANTITY, etQuantity.getText().toString());
        outState.putBoolean(KEY_IS_NEW, cbIsNew.isChecked());
        outState.putString(KEY_DESCRIPTION, etDescription.getText().toString());
    }

    private void submitDonation() {
        String goodsType = goodsTypeSpinner.getSelectedItem() != null ? goodsTypeSpinner.getSelectedItem().toString() : "";
        String quantity = etQuantity.getText().toString().trim();
        boolean isNew = cbIsNew.isChecked();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(goodsType) || goodsType.equals("Clothing")) {
            Toast.makeText(this, "Please select a goods type", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Goods donation submitted successfully", Toast.LENGTH_SHORT).show();
                navigationHelper.navigateToMain();
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Please enter a valid number");
        }
    }
}