package com.athisintiya.helpinghands.model;

public class ResourceLocation {
    private String id;
    private String name;
    private String type;
    private double latitude;
    private double longitude;
    private String address;
    private String phone;
    private String hours;
    private boolean isOpen;
    private double distance;
    private String description;

    public ResourceLocation() {
        // Default constructor required for Firestore
    }

    public ResourceLocation(String name, String type, double latitude, double longitude) {
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isOpen = true;
    }

    public ResourceLocation(String name, String type, double latitude, double longitude, String address) {
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.isOpen = true;
    }

    // Getters and Setters
    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type != null ? type : "";
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHours() {
        return hours != null ? hours : "";
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Helper methods
    public boolean isValid() {
        return !getName().isEmpty() &&
                !getType().isEmpty() &&
                latitude != 0 &&
                longitude != 0;
    }

    public String getStatus() {
        return isOpen ? "Open" : "Closed";
    }

    public String getFormattedDistance() {
        if (distance < 1) {
            return String.format("%.0f m", distance * 1000);
        } else {
            return String.format("%.1f km", distance);
        }
    }

    public String getTypeIcon() {
        switch (type.toLowerCase()) {
            case "shelter":
                return "🏠";
            case "healthcare":
            case "medical":
                return "🏥";
            case "food":
                return "🍽️";
            case "clothing":
                return "👕";
            default:
                return "📍";
        }
    }

    @Override
    public String toString() {
        return "ResourceLocation{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", address='" + address + '\'' +
                ", isOpen=" + isOpen +
                '}';
    }
}