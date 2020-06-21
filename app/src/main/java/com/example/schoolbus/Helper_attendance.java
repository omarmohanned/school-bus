package com.example.schoolbus;

import com.google.android.gms.maps.model.LatLng;

public class Helper_attendance {
    private double latitude;
    private double longitude;
    private String home_name;
    private int absent;

    public String getHome_name() {
        return home_name;
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

    public int getAbsent() {
        return absent;
    }
    public void setAbsent(int absent) {
        this.absent = absent;
    }
    public void setHome_name(String home_name) {
        this.home_name = home_name;
    }

    public Helper_attendance(double latitude,double longitude, String home_name,int absent) {
        this.latitude=latitude;
        this.longitude=longitude;
        this.absent=absent;
        this.home_name = home_name;
    }
}
