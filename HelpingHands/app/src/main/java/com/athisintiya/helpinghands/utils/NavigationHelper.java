package com.athisintiya.helpinghands.utils;

import android.content.Context;
import android.content.Intent;
import com.athisintiya.helpinghands.auth.LoginActivity;
import com.athisintiya.helpinghands.auth.SignupActivity;
import com.athisintiya.helpinghands.dashboard.AccountActivity;
import com.athisintiya.helpinghands.dashboard.DonateActivity;
import com.athisintiya.helpinghands.dashboard.MainActivity;
import com.athisintiya.helpinghands.dashboard.ReportActivity;
import com.athisintiya.helpinghands.dashboard.ResourcesActivity;
import com.athisintiya.helpinghands.dashboard.VolunteerActivity;
import com.athisintiya.helpinghands.dashboard.WelcomeActivity;
import com.athisintiya.helpinghands.dashboard.FoodDonationActivity;
import com.athisintiya.helpinghands.dashboard.GoodsDonationActivity;

public class NavigationHelper {
    private Context context;

    public NavigationHelper(Context context) {
        this.context = context;
    }

    public void navigateToLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        finishCurrentActivity();
    }

    public void navigateToSignup() {
        Intent intent = new Intent(context, SignupActivity.class);
        context.startActivity(intent);
    }

    public void navigateToMain() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        finishCurrentActivity();
    }

    public void navigateToWelcome() {
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        finishCurrentActivity();
    }

    public void navigateToResources() {
        Intent intent = new Intent(context, ResourcesActivity.class);
        context.startActivity(intent);
    }

    public void navigateToDonate() {
        Intent intent = new Intent(context, DonateActivity.class);
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

    public void navigateToReport() {
        Intent intent = new Intent(context, ReportActivity.class);
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

    public void finishCurrentActivity() {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).finish();
        }
    }
}