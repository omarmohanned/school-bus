package com.example.schoolbus;

import com.google.maps.model.LatLng;

public class Student {

    private String name;
    private int absent;
    private String parent_uid;
    private int id;
    private boolean is_selected;
    private LatLng location;

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Student(String studentName, String parent_uid, LatLng location,int absent) {

        this.absent=absent;
        this.parent_uid=parent_uid;
        this.name = studentName;
        this.location=location;

    }


    public String getStudentName() {
        return name;
    }

    public void setStudentId(int id) {
        this.id = id;
    }
    public int getStudentId() {
        return id;
    }

    public String getParent_uid() {
        return parent_uid;
    }

    public void setParent_uid(String parent_uid) {
        this.parent_uid = parent_uid;
    }
    public void setStudentName(String studentName) {
        this.name = studentName;
    }

    public boolean isIs_selected()
    {
        return is_selected;
    }

    public void setIs_selected(boolean selected)
    {
        is_selected=selected;
    }
}
