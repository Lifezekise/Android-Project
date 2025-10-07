package com.athisintiya.helpinghands.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.utils.NavigationHelper;

public class ReportActivity extends AppCompatActivity {
    private EditText etFullName, etCondition;
    private Spinner spGender;
    private Button btnShareLocation, btnSave;
    private NavigationHelper navigationHelper;
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_CONDITION = "condition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        navigationHelper = new NavigationHelper(this);

        // Fix text colors without needing IDs
        fixTextColors();

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        spGender = findViewById(R.id.spGender);
        btnShareLocation = findViewById(R.id.btnShareLocation);
        etCondition = findViewById(R.id.etCondition);
        btnSave = findViewById(R.id.btnSave);
        Button btnBack = findViewById(R.id.btnBack);

        // Set up gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(adapter);

        // Restore state if available
        if (savedInstanceState != null) {
            etFullName.setText(savedInstanceState.getString(KEY_FULL_NAME, ""));
            spGender.setSelection(savedInstanceState.getInt(KEY_GENDER, 0));
            etCondition.setText(savedInstanceState.getString(KEY_CONDITION, ""));
        }

        // Set click listeners
        btnShareLocation.setOnClickListener(v -> shareLocation());
        btnSave.setOnClickListener(v -> saveReport());
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
        outState.putString(KEY_FULL_NAME, etFullName.getText().toString());
        outState.putInt(KEY_GENDER, spGender.getSelectedItemPosition());
        outState.putString(KEY_CONDITION, etCondition.getText().toString());
    }

    private void shareLocation() {
        Toast.makeText(this, "Location sharing functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    private void saveReport() {
        String fullName = etFullName.getText().toString().trim();
        String gender = spGender.getSelectedItem() != null ? spGender.getSelectedItem().toString() : "";
        String condition = etCondition.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Please enter full name");
            return;
        }

        if (TextUtils.isEmpty(gender) || gender.equals("Select gender")) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(condition)) {
            etCondition.setError("Please describe condition");
            return;
        }

        Toast.makeText(this, "Report saved successfully", Toast.LENGTH_SHORT).show();
        navigationHelper.navigateToMain();
    }
}