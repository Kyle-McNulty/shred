package com.kjmcnult.uw.edu.shredio;

/**
 * Created by sii92_000 on 5/31/2017.
 */

public class LatLng {
    private Double latitude;
    private Double longitude;

    public LatLng() { }

    public LatLng(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public com.google.android.gms.maps.model.LatLng getLatLng() {
        return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
    }
}
