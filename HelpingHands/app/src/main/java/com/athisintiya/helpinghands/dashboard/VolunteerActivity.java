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
import com.athisintiya.helpinghands.utils.NavigationHelper;

public class VolunteerActivity extends AppCompatActivity {
    private Spinner spRole;
    private CheckBox cbTerms;
    private Button btnVolunteer;
    private TextView tvSkip;
    private NavigationHelper navigationHelper;
    private static final String KEY_ROLE = "role";
    private static final String KEY_TERMS = "terms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        navigationHelper = new NavigationHelper(this);

        fixTextColors();

        spRole = findViewById(R.id.spRole);
        cbTerms = findViewById(R.id.cbTerms);
        btnVolunteer = findViewById(R.id.btnVolunteer);
        tvSkip = findViewById(R.id.tvSkip);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.volunteer_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);

        if (savedInstanceState != null) {
            spRole.setSelection(savedInstanceState.getInt(KEY_ROLE, 0));
            cbTerms.setChecked(savedInstanceState.getBoolean(KEY_TERMS, false));
        }

        btnVolunteer.setOnClickListener(v -> handleVolunteer());
        tvSkip.setOnClickListener(v -> navigationHelper.navigateToMain());
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
        outState.putInt(KEY_ROLE, spRole.getSelectedItemPosition());
        outState.putBoolean(KEY_TERMS, cbTerms.isChecked());
    }

    private void handleVolunteer() {
        String role = spRole.getSelectedItem() != null ? spRole.getSelectedItem().toString() : "";
        if (TextUtils.isEmpty(role) || role.equals("--select--")) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "You must agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Thank you for volunteering as a " + role + "!", Toast.LENGTH_LONG).show();
        navigationHelper.navigateToMain();
    }
}