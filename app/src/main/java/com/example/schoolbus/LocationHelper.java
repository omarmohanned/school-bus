package com.example.schoolbus;

import com.google.android.gms.maps.model.LatLng;

public class LocationHelper {
//    private double longitude;
//    private double latitude;
    private LatLng latLng;
    private String paren_uid;
    private String home_name;

    public String getHome_name() {
        return home_name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getParen_uid() {
        return paren_uid;
    }

    public void setParen_uid(String paren_uid) {
        this.paren_uid = paren_uid;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setHome_name(String home_name) {
        this.home_name = home_name;
    }

    public LocationHelper(LatLng latLng, String home_name,String paren_uid) {
        this.paren_uid=paren_uid;
        this.latLng = latLng;
        this.home_name = home_name;
    }
}
