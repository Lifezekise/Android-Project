package com.athisintiya.helpinghands.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.athisintiya.helpinghands.auth.LoginActivity;
import com.athisintiya.helpinghands.auth.SignupActivity;
import com.athisintiya.helpinghands.dashboard.AccountActivity;
import com.athisintiya.helpinghands.dashboard.DonateActivity;
import com.athisintiya.helpinghands.dashboard.FoodDonationActivity;
import com.athisintiya.helpinghands.dashboard.GoodsDonationActivity;
import com.athisintiya.helpinghands.dashboard.MainActivity;
import com.athisintiya.helpinghands.dashboard.MoneyDonationActivity;
import com.athisintiya.helpinghands.dashboard.ReportActivity;
import com.athisintiya.helpinghands.dashboard.ResourcesActivity;
import com.athisintiya.helpinghands.dashboard.VolunteerActivity;
import com.athisintiya.helpinghands.dashboard.WelcomeActivity;

public class NavigationHelper {
    private Context context;

    public NavigationHelper(Context context) {
        this.context = context;
    }

    public void navigateToLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void navigateToSignup() {
        Intent intent = new Intent(context, SignupActivity.class);
        context.startActivity(intent);
    }

    public void navigateToMain() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void navigateToWelcome() {
        Intent intent = new Intent(context, WelcomeActivity.class);
        context.startActivity(intent);
    }

    public void navigateToReport() {
        Intent intent = new Intent(context, ReportActivity.class);
        context.startActivity(intent);
    }

    public void navigateToResources() {
        Intent intent = new Intent(context, ResourcesActivity.class);
        context.startActivity(intent);
    }

    public void navigateToDonate() {
        Intent intent = new Intent(context, DonateActivity.class);
        context.startActivity(intent);
    }

    public void navigateToMoneyDonation() {
        Intent intent = new Intent(context, MoneyDonationActivity.class);
        context.startActivity(intent);
    }

    public void navigateToFoodDonation() {
        Intent intent = new Intent(context, FoodDonationActivity.class);
        context.startActivity(intent);
    }

    public void navigateToGoodsDonation() {
        Intent intent = new Intent(context, GoodsDonationActivity.class);
        context.startActivity(intent);
    }

    public void navigateToVolunteer() {
        Intent intent = new Intent(context, VolunteerActivity.class);
        context.startActivity(intent);
    }

    public void navigateToAccount() {
        Intent intent = new Intent(context, AccountActivity.class);
        context.startActivity(intent);
    }

    public void navigateToDashboard() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public void navigateToEmergencyCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:10111"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}