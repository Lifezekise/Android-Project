package com.athisintiya.helpinghands.dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.databinding.ActivityDonateBinding;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.athisintiya.helpinghands.utils.SessionManager;

public class DonateActivity extends AppCompatActivity {
    private ActivityDonateBinding binding;
    private NavigationHelper navigationHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationHelper = new NavigationHelper(this);
        sessionManager = new SessionManager(this);

        binding.moneyBtn.setOnClickListener(v -> navigationHelper.navigateToMoneyDonation());
        binding.foodButton.setOnClickListener(v -> navigationHelper.navigateToFoodDonation());
        binding.goodsButton.setOnClickListener(v -> navigationHelper.navigateToGoodsDonation());
        binding.backButton.setOnClickListener(v -> navigationHelper.navigateToMain());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}