package com.athisintiya.helpinghands.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.databinding.ActivityVolunteerBinding;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class VolunteerActivity extends AppCompatActivity {
    private ActivityVolunteerBinding binding;
    private NavigationHelper navigationHelper;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String KEY_ROLE = "role";
    private static final String KEY_TERMS = "terms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVolunteerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationHelper = new NavigationHelper(this);
        sessionManager = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.volunteer_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spRole.setAdapter(adapter);

        binding.btnVolunteer.setOnClickListener(v -> handleVolunteer());
        binding.tvSkip.setOnClickListener(v -> navigationHelper.navigateToMain());

        if (savedInstanceState != null) {
            binding.spRole.setSelection(savedInstanceState.getInt(KEY_ROLE));
            binding.cbTerms.setChecked(savedInstanceState.getBoolean(KEY_TERMS));
        }
    }

    private void handleVolunteer() {
        String role = binding.spRole.getSelectedItem() != null ? binding.spRole.getSelectedItem().toString() : "";
        if (TextUtils.isEmpty(role) || role.equals("--select--")) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Select a role")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        if (!binding.cbTerms.isChecked()) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("You must agree to the Terms and Conditions")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        com.google.firebase.auth.FirebaseUser fbUser = mAuth.getCurrentUser();
        if (fbUser != null) {
            Map<String, Object> volunteer = new HashMap<>();
            volunteer.put("userId", fbUser.getUid());
            volunteer.put("role", role);
            volunteer.put("timestamp", System.currentTimeMillis());
            db.collection("volunteers").add(volunteer);

            new MaterialAlertDialogBuilder(this)
                    .setMessage("Volunteered as " + role)
                    .setPositiveButton("OK", (dialog, which) -> navigationHelper.navigateToMain())
                    .show();
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("User not logged in")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ROLE, binding.spRole.getSelectedItemPosition());
        outState.putBoolean(KEY_TERMS, binding.cbTerms.isChecked());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}