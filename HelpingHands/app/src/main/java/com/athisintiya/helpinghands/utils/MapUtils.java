package com.athisintiya.helpinghands.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.athisintiya.helpinghands.R;
import com.athisintiya.helpinghands.model.ResourceLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtils {

    private static Map<String, Marker> markerMap = new HashMap<>();

    public static void setupMap(GoogleMap googleMap, Context context) {
        if (googleMap == null) return;

        // Enable user location
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        // Map customization
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        // Set map style from XML
        setMapStyle(googleMap, context);

        // Set initial position to Cape Town
        LatLng capeTown = new LatLng(-33.9249, 18.4241);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(capeTown, 12f));
    }

    private static void setMapStyle(GoogleMap googleMap, Context context) {
        try {
            // Load map style from XML string resource
            String styleJson = context.getString(R.string.map_style_json);
            MapStyleOptions style = new MapStyleOptions(styleJson);
            googleMap.setMapStyle(style);
        } catch (Exception e) {
            e.printStackTrace();
            // Continue without custom style if there's an error
        }
    }

    public static void addResourceMarkers(GoogleMap googleMap, List<ResourceLocation> resources) {
        if (googleMap == null || resources == null) return;

        clearAllMarkers(googleMap);
        markerMap.clear();

        for (ResourceLocation resource : resources) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(resource.getLatitude(), resource.getLongitude()))
                    .title(resource.getName())
                    .snippet(resource.getType())
                    .icon(BitmapDescriptorFactory.fromResource(getMarkerIcon(resource.getType()))));

            if (marker != null) {
                markerMap.put(resource.getName(), marker);
            }
        }
    }

    public static void addSingleMarker(GoogleMap googleMap, ResourceLocation resource) {
        if (googleMap == null || resource == null) return;

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(resource.getLatitude(), resource.getLongitude()))
                .title(resource.getName())
                .snippet(resource.getType())
                .icon(BitmapDescriptorFactory.fromResource(getMarkerIcon(resource.getType()))));

        if (marker != null) {
            markerMap.put(resource.getName(), marker);
        }
    }

    private static int getMarkerIcon(String type) {
        if (type == null) return R.drawable.ic_marker_default;

        switch (type.toLowerCase()) {
            case "shelter":
                return R.drawable.ic_marker_shelter;
            case "healthcare":
                return R.drawable.ic_marker_healthcare;
            case "food":
                return R.drawable.ic_marker_food;
            case "emergency":
                return R.drawable.ic_marker_emergency;
            case "report":
                return R.drawable.ic_marker_report;
            default:
                return R.drawable.ic_marker_default;
        }
    }

    // Additional utility methods
    public static void centerMapOnLocation(GoogleMap googleMap, double latitude, double longitude, float zoomLevel) {
        if (googleMap != null) {
            LatLng location = new LatLng(latitude, longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
        }
    }

    public static void clearAllMarkers(GoogleMap googleMap) {
        if (googleMap != null) {
            googleMap.clear();
            markerMap.clear();
        }
    }

    public static void fitAllMarkers(GoogleMap googleMap) {
        if (googleMap == null || markerMap.isEmpty()) return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerMap.values()) {
            builder.include(marker.getPosition());
        }

        try {
            LatLngBounds bounds = builder.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to Cape Town if no valid bounds
            centerMapOnLocation(googleMap, -33.9249, 18.4241, 12f);
        }
    }

    public static Circle addRadiusCircle(GoogleMap googleMap, LatLng location, double radiusInMeters) {
        if (googleMap == null) return null;

        return googleMap.addCircle(new CircleOptions()
                .center(location)
                .radius(radiusInMeters)
                .strokeColor(Color.argb(50, 70, 130, 180))
                .fillColor(Color.argb(20, 70, 130, 180))
                .strokeWidth(2f));
    }

    public static List<ResourceLocation> getSampleCapeTownResources() {
        List<ResourceLocation> resources = new ArrayList<>();

        // Sample shelters in Cape Town
        resources.add(new ResourceLocation("The Haven Night Shelter", "shelter", -33.9258, 18.4239));
        resources.add(new ResourceLocation("Cape Town City Mission", "shelter", -33.9281, 18.4225));
        resources.add(new ResourceLocation("Homeless Action Committee", "shelter", -33.9194, 18.4218));

        // Sample food resources
        resources.add(new ResourceLocation("St. George's Cathedral Soup Kitchen", "food", -33.9250, 18.4245));
        resources.add(new ResourceLocation("Ladles of Love", "food", -33.9178, 18.4256));

        // Sample healthcare
        resources.add(new ResourceLocation("Red Cross Children's Hospital", "healthcare", -33.9275, 18.4612));
        resources.add(new ResourceLocation("Groote Schuur Hospital", "healthcare", -33.9352, 18.4651));
        resources.add(new ResourceLocation("City Health Clinic", "healthcare", -33.9215, 18.4278));

        return resources;
    }

    // Method to change map style dynamically
    public static void setMapStyleFromJson(GoogleMap googleMap, String jsonStyle) {
        if (googleMap != null && jsonStyle != null) {
            try {
                MapStyleOptions style = new MapStyleOptions(jsonStyle);
                googleMap.setMapStyle(style);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}