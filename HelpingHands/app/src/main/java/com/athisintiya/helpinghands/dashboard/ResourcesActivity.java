package com.athisintiya.helpinghands.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.databinding.ActivityResourcesBinding;
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
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class ResourcesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityResourcesBinding binding;
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

        binding = ActivityResourcesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        setupClickListeners();
        setupChipListeners();
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

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> navigationHelper.navigateToDashboard());

        binding.btnSearch.setOnClickListener(v -> performSearch());

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });

        binding.btnDirections.setOnClickListener(v -> {
            Toast.makeText(this, "Getting directions to nearest resource...", Toast.LENGTH_SHORT).show();
        });

        binding.btnGetDirections.setOnClickListener(v -> {
            openDirectionsInMaps();
        });

        binding.fabCurrentLocation.setOnClickListener(v -> {
            getLastLocation();
        });
    }

    private void setupChipListeners() {
        // Set up chip listeners for filtering
        binding.chipShelters.setOnClickListener(v -> {
            showShelters();
            updateResourceCard("shelter");
        });

        binding.chipHealthcare.setOnClickListener(v -> {
            showHealthcare();
            updateResourceCard("healthcare");
        });

        binding.chipFood.setOnClickListener(v -> {
            showFood();
            updateResourceCard("food");
        });

        binding.chipAll.setOnClickListener(v -> {
            showAllResources();
            updateResourceCard("all");
        });

        // Set "All" as selected by default
        binding.chipAll.setChecked(true);
    }

    private void performSearch() {
        String query = binding.etSearch.getText().toString().trim();
        if (!TextUtils.isEmpty(query)) {
            searchPlaces(query);
            updateResourceCardForSearch(query);
        } else {
            Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeSampleData() {
        // Shelter locations
        shelterLocations.add(new PlaceLocation("Cape Town Central Shelter", -33.9258, 18.4239, "shelter", "123 Long Street, Cape Town"));
        shelterLocations.add(new PlaceLocation("Hope Shelter", -33.9186, 18.4272, "shelter", "45 Buitenkant Street, Cape Town"));
        shelterLocations.add(new PlaceLocation("Safe Haven", -33.9312, 18.4198, "shelter", "78 Roeland Street, Cape Town"));

        // Healthcare locations
        healthcareLocations.add(new PlaceLocation("Cape Town Clinic", -33.9276, 18.4215, "healthcare", "101 Darling Street, Cape Town"));
        healthcareLocations.add(new PlaceLocation("Community Health Center", -33.9221, 18.4257, "healthcare", "234 Plein Street, Cape Town"));
        healthcareLocations.add(new PlaceLocation("Emergency Medical", -33.9198, 18.4223, "healthcare", "567 Strand Street, Cape Town"));

        // Food locations
        foodLocations.add(new PlaceLocation("Food Distribution Center", -33.9263, 18.4201, "food", "89 Bree Street, Cape Town"));
        foodLocations.add(new PlaceLocation("Community Kitchen", -33.9234, 18.4248, "food", "321 Loop Street, Cape Town"));
        foodLocations.add(new PlaceLocation("Food Bank", -33.9205, 18.4212, "food", "654 Church Street, Cape Town"));
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
            updateResourceCard("all");

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
        updateChipSelection(binding.chipShelters);
    }

    private void showHealthcare() {
        showLocations(healthcareLocations, "Showing healthcare facilities");
        updateChipSelection(binding.chipHealthcare);
    }

    private void showFood() {
        showLocations(foodLocations, "Showing food distribution points");
        updateChipSelection(binding.chipFood);
    }

    private void showAllResources() {
        showLocations(getAllLocations(), "Showing all resources in Cape Town");
        updateChipSelection(binding.chipAll);
    }

    private void updateChipSelection(Chip selectedChip) {
        // Uncheck all chips
        binding.chipShelters.setChecked(false);
        binding.chipHealthcare.setChecked(false);
        binding.chipFood.setChecked(false);
        binding.chipAll.setChecked(false);

        // Check the selected chip
        selectedChip.setChecked(true);
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
                .snippet(place.address);

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

    private void updateResourceCard(String resourceType) {
        binding.cardNearestResource.setVisibility(View.VISIBLE);

        switch (resourceType) {
            case "shelter":
                binding.ivResourceIcon.setImageResource(R.drawable.ic_shelter);
                binding.ivResourceIcon.setColorFilter(ContextCompat.getColor(this, R.color.success));
                binding.tvResourceName.setText("Hope Shelter Cape Town");
                binding.tvResourceAddress.setText("45 Buitenkant Street, Cape Town");
                binding.tvResourceDistance.setText("0.8 km away");
                binding.tvResourceStatus.setText("🟢 Open Now");
                binding.tvResourceStatus.setTextColor(ContextCompat.getColor(this, R.color.success));
                binding.tvResourceHours.setText("Open until 10 PM");
                break;

            case "healthcare":
                binding.ivResourceIcon.setImageResource(R.drawable.ic_healthcare);
                binding.ivResourceIcon.setColorFilter(ContextCompat.getColor(this, R.color.error));
                binding.tvResourceName.setText("City Health Clinic");
                binding.tvResourceAddress.setText("101 Darling Street, Cape Town");
                binding.tvResourceDistance.setText("1.5 km away");
                binding.tvResourceStatus.setText("🟡 Closes at 6 PM");
                binding.tvResourceStatus.setTextColor(ContextCompat.getColor(this, R.color.warning));
                binding.tvResourceHours.setText("Open until 6 PM");
                break;

            case "food":
                binding.ivResourceIcon.setImageResource(R.drawable.ic_food);
                binding.ivResourceIcon.setColorFilter(ContextCompat.getColor(this, R.color.warning));
                binding.tvResourceName.setText("Community Food Bank");
                binding.tvResourceAddress.setText("89 Bree Street, Cape Town");
                binding.tvResourceDistance.setText("1.2 km away");
                binding.tvResourceStatus.setText("🟢 Open Now");
                binding.tvResourceStatus.setTextColor(ContextCompat.getColor(this, R.color.success));
                binding.tvResourceHours.setText("Open until 8 PM");
                break;

            case "all":
            default:
                binding.ivResourceIcon.setImageResource(R.drawable.ic_all);
                binding.ivResourceIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary));
                binding.tvResourceName.setText("Cape Town Central Shelter");
                binding.tvResourceAddress.setText("123 Long Street, Cape Town");
                binding.tvResourceDistance.setText("1.2 km away");
                binding.tvResourceStatus.setText("🟢 Open Now");
                binding.tvResourceStatus.setTextColor(ContextCompat.getColor(this, R.color.success));
                binding.tvResourceHours.setText("Open until 10 PM");
                break;
        }
    }

    private void updateResourceCardForSearch(String query) {
        binding.cardNearestResource.setVisibility(View.VISIBLE);

        // Find the first matching location for the search query
        List<PlaceLocation> allLocations = getAllLocations();
        for (PlaceLocation location : allLocations) {
            if (location.name.toLowerCase().contains(query.toLowerCase())) {
                updateCardWithLocation(location);
                return;
            }
        }

        // If no exact match, show default
        updateResourceCard("all");
    }

    private void updateCardWithLocation(PlaceLocation location) {
        binding.cardNearestResource.setVisibility(View.VISIBLE);

        switch (location.type) {
            case "shelter":
                binding.ivResourceIcon.setImageResource(R.drawable.ic_shelter);
                binding.ivResourceIcon.setColorFilter(ContextCompat.getColor(this, R.color.success));
                break;
            case "healthcare":
                binding.ivResourceIcon.setImageResource(R.drawable.ic_healthcare);
                binding.ivResourceIcon.setColorFilter(ContextCompat.getColor(this, R.color.error));
                break;
            case "food":
                binding.ivResourceIcon.setImageResource(R.drawable.ic_food);
                binding.ivResourceIcon.setColorFilter(ContextCompat.getColor(this, R.color.warning));
                break;
            default:
                binding.ivResourceIcon.setImageResource(R.drawable.ic_all);
                binding.ivResourceIcon.setColorFilter(ContextCompat.getColor(this, R.color.primary));
                break;
        }

        binding.tvResourceName.setText(location.name);
        binding.tvResourceAddress.setText(location.address);
        binding.tvResourceDistance.setText("1.2 km away"); // This would be calculated in real app
        binding.tvResourceStatus.setText("🟢 Open Now");
        binding.tvResourceStatus.setTextColor(ContextCompat.getColor(this, R.color.success));
        binding.tvResourceHours.setText("Open until 10 PM");
    }

    private void openDirectionsInMaps() {
        String address = binding.tvResourceAddress.getText().toString();

        // Create URI for Google Maps
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Check if Google Maps is installed
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback: Open in browser
            Uri webIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + Uri.encode(address));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webIntentUri);
            startActivity(webIntent);
        }
    }

    private void fixTextColors() {
        ViewGroup rootView = findViewById(android.R.id.content);
        changeTextColorsRecursive(rootView);
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
        if (binding.etSearch != null) {
            outState.putString(KEY_SEARCH_TEXT, binding.etSearch.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedText = savedInstanceState.getString(KEY_SEARCH_TEXT, "");
        if (binding.etSearch != null) {
            binding.etSearch.setText(savedText);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private static class PlaceLocation {
        final String name;
        final double latitude;
        final double longitude;
        final String type;
        final String address;

        PlaceLocation(String name, double latitude, double longitude, String type, String address) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.type = type;
            this.address = address;
        }

        LatLng getLatLng() {
            return new LatLng(latitude, longitude);
        }
    }
}