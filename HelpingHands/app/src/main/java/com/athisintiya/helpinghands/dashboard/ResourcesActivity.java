package com.athisintiya.helpinghands.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ResourcesActivity extends AppCompatActivity implements OnMapReadyCallback {
    private EditText etSearch;
    private Button btnBack;
    private NavigationHelper navigationHelper;
    private static final String KEY_SEARCH_TEXT = "search_text";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final LatLng CAPE_TOWN_CENTER = new LatLng(-33.9249, 18.4241);

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private boolean locationPermissionGranted = false;

    // Sample locations for demonstration
    private final List<PlaceLocation> shelterLocations = new ArrayList<>();
    private final List<PlaceLocation> healthcareLocations = new ArrayList<>();
    private final List<PlaceLocation> foodLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        // Check for location permissions
        checkLocationPermissions();

        navigationHelper = new NavigationHelper(this);
        fixTextColors();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize map fragment safely
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        initializeSampleData();
        setupViews();
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                if (mMap != null) {
                    enableMyLocation();
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupViews() {
        try {
            etSearch = findViewById(R.id.etSearch);
            btnBack = findViewById(R.id.btnBack);
            Button btnShelters = findViewById(R.id.btnShelters);
            Button btnHealthcare = findViewById(R.id.btnHealthcare);
            Button btnFood = findViewById(R.id.btnFood);
            Button btnAll = findViewById(R.id.btnAll);
            Button btnSearch = findViewById(R.id.btnSearch);
            Button btnDirections = findViewById(R.id.btnDirections);

            if (getIntent().getExtras() != null) {
                String savedText = getIntent().getExtras().getString(KEY_SEARCH_TEXT, "");
                etSearch.setText(savedText);
            }

            btnBack.setOnClickListener(v -> navigationHelper.navigateToMain());

            btnSearch.setOnClickListener(v -> performSearch());

            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                performSearch();
                return true;
            });

            btnShelters.setOnClickListener(v -> showShelters());
            btnHealthcare.setOnClickListener(v -> showHealthcare());
            btnFood.setOnClickListener(v -> showFood());
            btnAll.setOnClickListener(v -> showAllResources());

            btnDirections.setOnClickListener(v -> {
                Toast.makeText(this, "Directions feature coming soon!", Toast.LENGTH_SHORT).show();
            });

        } catch (Exception e) {
            Log.e("ResourcesActivity", "Error setting up views: " + e.getMessage());
            Toast.makeText(this, "Error initializing screen", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        if (!TextUtils.isEmpty(query)) {
            searchPlaces(query);
        } else {
            Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeSampleData() {
        // Shelter locations
        shelterLocations.add(new PlaceLocation("Cape Town Shelter", -33.9258, 18.4239, "shelter"));
        shelterLocations.add(new PlaceLocation("Hope Shelter", -33.9186, 18.4272, "shelter"));
        shelterLocations.add(new PlaceLocation("Safe Haven", -33.9312, 18.4198, "shelter"));

        // Healthcare locations
        healthcareLocations.add(new PlaceLocation("Cape Town Clinic", -33.9276, 18.4215, "healthcare"));
        healthcareLocations.add(new PlaceLocation("Community Health Center", -33.9221, 18.4257, "healthcare"));
        healthcareLocations.add(new PlaceLocation("Emergency Medical", -33.9198, 18.4223, "healthcare"));

        // Food locations
        foodLocations.add(new PlaceLocation("Food Distribution Center", -33.9263, 18.4201, "food"));
        foodLocations.add(new PlaceLocation("Community Kitchen", -33.9234, 18.4248, "food"));
        foodLocations.add(new PlaceLocation("Food Bank", -33.9205, 18.4212, "food"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Configure map
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);

            // Move camera to Cape Town
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CAPE_TOWN_CENTER, 12));

            if (locationPermissionGranted) {
                enableMyLocation();
            }

            // Show all resources by default
            showAllResources();

        } catch (Exception e) {
            Log.e("ResourcesActivity", "Error in onMapReady: " + e.getMessage());
            Toast.makeText(this, "Map initialization error", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (mMap != null && locationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (!locationPermissionGranted) {
            return;
        }

        try {
            Task<Location> locationTask = fusedLocationClient.getLastLocation();
            locationTask.addOnSuccessListener(this, location -> {
                if (location != null && mMap != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // Add a marker for current location
                    mMap.addMarker(new MarkerOptions()
                            .position(currentLatLng)
                            .title("Your Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    // Zoom to current location
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));
                }
            });
        } catch (SecurityException e) {
            Log.e("ResourcesActivity", "Location permission not granted: " + e.getMessage());
        } catch (Exception e) {
            Log.e("ResourcesActivity", "Error getting location: " + e.getMessage());
        }
    }

    private void searchPlaces(String query) {
        if (mMap == null) return;

        mMap.clear();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        int resultsCount = 0;

        // Search through all locations
        List<PlaceLocation> allLocations = getAllLocations();
        for (PlaceLocation place : allLocations) {
            if (place.name.toLowerCase().contains(query.toLowerCase())) {
                addMarkerForPlace(place);
                boundsBuilder.include(place.getLatLng());
                resultsCount++;
            }
        }

        if (resultsCount > 0) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
            Toast.makeText(this, "Found " + resultsCount + " results for: " + query, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No results found for: " + query, Toast.LENGTH_SHORT).show();
            showAllResources(); // Show all if no results
        }
    }

    private void showShelters() {
        showLocations(shelterLocations, "Showing shelters in Cape Town");
    }

    private void showHealthcare() {
        showLocations(healthcareLocations, "Showing healthcare facilities");
    }

    private void showFood() {
        showLocations(foodLocations, "Showing food distribution points");
    }

    private void showAllResources() {
        showLocations(getAllLocations(), "Showing all resources in Cape Town");
    }

    private void showLocations(List<PlaceLocation> locations, String message) {
        if (mMap == null) return;

        mMap.clear();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (PlaceLocation location : locations) {
            addMarkerForPlace(location);
            boundsBuilder.include(location.getLatLng());
        }

        if (!locations.isEmpty()) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private List<PlaceLocation> getAllLocations() {
        List<PlaceLocation> allLocations = new ArrayList<>();
        allLocations.addAll(shelterLocations);
        allLocations.addAll(healthcareLocations);
        allLocations.addAll(foodLocations);
        return allLocations;
    }

    private void addMarkerForPlace(PlaceLocation place) {
        if (mMap == null) return;

        MarkerOptions markerOptions = new MarkerOptions()
                .position(place.getLatLng())
                .title(place.name)
                .snippet(place.type);

        // Set different colors for different types
        switch (place.type) {
            case "shelter":
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                break;
            case "healthcare":
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                break;
            case "food":
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                break;
            default:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                break;
        }

        mMap.addMarker(markerOptions);
    }

    private void fixTextColors() {
        ViewGroup rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            changeTextColorsRecursive(rootView);
        }
    }

    private void changeTextColorsRecursive(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            if (child instanceof TextView) {
                ((TextView) child).setTextColor(Color.BLACK);
            } else if (child instanceof EditText) {
                ((EditText) child).setTextColor(Color.BLACK);
                ((EditText) child).setHintTextColor(Color.GRAY);
            } else if (child instanceof ViewGroup) {
                changeTextColorsRecursive((ViewGroup) child);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (etSearch != null) {
            outState.putString(KEY_SEARCH_TEXT, etSearch.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedText = savedInstanceState.getString(KEY_SEARCH_TEXT, "");
        if (etSearch != null) {
            etSearch.setText(savedText);
        }
    }

    private static class PlaceLocation {
        final String name;
        final double latitude;
        final double longitude;
        final String type;

        PlaceLocation(String name, double latitude, double longitude, String type) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.type = type;
        }

        LatLng getLatLng() {
            return new LatLng(latitude, longitude);
        }
    }
}