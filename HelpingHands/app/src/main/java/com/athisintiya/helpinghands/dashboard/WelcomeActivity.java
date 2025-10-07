package com.athisintiya.helpinghands.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.utils.NavigationHelper;

public class WelcomeActivity extends AppCompatActivity {
    private Button btnReport, btnSkip;
    private NavigationHelper navigationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        navigationHelper = new NavigationHelper(this);

        fixTextColors();

        btnReport = findViewById(R.id.btnReport);
        btnSkip = findViewById(R.id.btnSkip);

        btnReport.setOnClickListener(v -> navigationHelper.navigateToReport());
        btnSkip.setOnClickListener(v -> navigationHelper.navigateToMain());
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
            if (child instanceof Button) {
                ((Button) child).setTextColor(Color.BLACK);
            }
            if (child instanceof ViewGroup) {
                changeTextColorsRecursive((ViewGroup) child);
            }
        }
    }
}