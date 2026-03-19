package com.athisintiya.helpinghands.dashboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.databinding.ActivityReportBinding;
import com.athisintiya.helpinghands.utils.NavigationHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityReportBinding binding;
    private NavigationHelper navigationHelper;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng selectedLocation;
    private FirebaseFirestore db;
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_CONDITION = "condition";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getUserLocation();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigationHelper = new NavigationHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spGender.setAdapter(adapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        binding.btnSave.setOnClickListener(v -> saveReport());
        binding.btnBack.setOnClickListener(v -> navigationHelper.navigateToMain());

        if (savedInstanceState != null) {
            binding.etFullName.setText(savedInstanceState.getString(KEY_FULL_NAME));
            // For AutoCompleteTextView, set text instead of selection
            if (savedInstanceState.getString(KEY_GENDER) != null) {
                binding.spGender.setText(savedInstanceState.getString(KEY_GENDER));
            }
            binding.etCondition.setText(savedInstanceState.getString(KEY_CONDITION));
            double lat = savedInstanceState.getDouble(KEY_LAT, 0);
            double lng = savedInstanceState.getDouble(KEY_LNG, 0);
            if (lat != 0 && lng != 0) {
                selectedLocation = new LatLng(lat, lng);
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.9249, 18.4241), 12)); // Cape Town

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getUserLocation();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            selectedLocation = latLng;
        });
    }

    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
                }
            });
        }
    }

    private void saveReport() {
        String fullName = binding.etFullName.getText().toString().trim();
        String gender = binding.spGender.getText().toString().trim();
        String condition = binding.etCondition.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            binding.etFullName.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(gender) || gender.equals("Select gender")) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Select gender")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(condition)) {
            binding.etCondition.setError("Required");
            return;
        }
        if (selectedLocation == null) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Select a location on the map")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Save report to Firestore
        Map<String, Object> report = new HashMap<>();
        report.put("fullName", fullName);
        report.put("gender", gender);
        report.put("condition", condition);
        report.put("latitude", selectedLocation.latitude);
        report.put("longitude", selectedLocation.longitude);
        db.collection("reports").add(report);

        // Subscribe to push notifications
        FirebaseMessaging.getInstance().subscribeToTopic("help_requests");
        new MaterialAlertDialogBuilder(this)
                .setTitle("Report Submitted")
                .setMessage("Help request sent for " + fullName)
                .setPositiveButton("OK", (dialog, which) -> navigationHelper.navigateToMain())
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FULL_NAME, binding.etFullName.getText().toString());
        // Save the text for AutoCompleteTextView instead of position
        outState.putString(KEY_GENDER, binding.spGender.getText().toString());
        outState.putString(KEY_CONDITION, binding.etCondition.getText().toString());
        if (selectedLocation != null) {
            outState.putDouble(KEY_LAT, selectedLocation.latitude);
            outState.putDouble(KEY_LNG, selectedLocation.longitude);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
