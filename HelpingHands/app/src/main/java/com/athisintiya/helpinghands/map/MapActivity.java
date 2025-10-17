package com.athisintiya.helpinghands.map;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.model.ResourceLocation;
import com.athisintiya.helpinghands.utils.MapUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private List<ResourceLocation> resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize sample data
        initializeSampleResources();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Setup map using utility class
        MapUtils.setupMap(googleMap, this);

        // Add markers
        List<ResourceLocation> resources = MapUtils.getSampleCapeTownResources();
        MapUtils.addResourceMarkers(googleMap, resources);

        // Set up marker click listener
        googleMap.setOnMarkerClickListener(marker -> {
            // Handle marker click
            showResourceDetails(marker.getTitle());
            return true;
        });
    }

    private void initializeSampleResources() {
        resources = new ArrayList<>();

        // Add sample shelters in Cape Town
        resources.add(new ResourceLocation(
                "The Haven Night Shelter",
                "shelter",
                -33.9258,
                18.4239
        ));

        resources.add(new ResourceLocation(
                "Cape Town City Mission",
                "shelter",
                -33.9281,
                18.4225
        ));

        resources.add(new ResourceLocation(
                "St. George's Cathedral Soup Kitchen",
                "food",
                -33.9250,
                18.4245
        ));

        resources.add(new ResourceLocation(
                "Red Cross Children's Hospital",
                "healthcare",
                -33.9275,
                18.4612
        ));
    }

    private void showResourceDetails(String resourceName) {
        // Show details in a dialog or bottom sheet
        new AlertDialog.Builder(this)
                .setTitle(resourceName)
                .setMessage("Resource details would go here")
                .setPositiveButton("OK", null)
                .show();
    }
}
