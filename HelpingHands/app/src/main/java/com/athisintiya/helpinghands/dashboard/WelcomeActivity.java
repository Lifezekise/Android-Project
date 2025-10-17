package com.athisintiya.helpinghands.dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.databinding.ActivityWelcomeBinding;
import com.athisintiya.helpinghands.utils.NavigationHelper;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding binding;
    private NavigationHelper navigationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationHelper = new NavigationHelper(this);

        binding.btnReport.setOnClickListener(v -> navigationHelper.navigateToReport());
        binding.btnSkip.setOnClickListener(v -> navigationHelper.navigateToMain());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}