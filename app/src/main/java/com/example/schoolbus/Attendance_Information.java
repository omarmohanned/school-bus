package com.example.schoolbus;

public class Attendance_Information {
   // private int id;
    private String StudentName;
    private String attendance_date;
    private String parent_uid;


    public Attendance_Information(String studentName) {
        //this.id = id;
        this.StudentName = studentName;
        this.attendance_date=attendance_date;
        this.parent_uid=parent_uid;
        //this.attendance_date = attendance_date;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getAttendance_date() {
        return attendance_date;
    }

    public void setAttendance_date(String attendance_date) {
        this.attendance_date = attendance_date;
    }

    public void setParent_uid(String parent_uid) {
        this.parent_uid = parent_uid;
    }

    public String getParent_uid() {
        return parent_uid;
    }
}
