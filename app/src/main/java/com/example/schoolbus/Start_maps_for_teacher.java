package com.example.schoolbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.schoolbus.Main_Teacher.*;
import static com.example.schoolbus.Attendance.*;

public class Start_maps_for_teacher extends AppCompatActivity {


    public static int maps_counter_goes=0;
    public static int trips_type_for_maps=1;
    Spinner trip_type;
    public static String bus_driver_uid_to_start_trip ;
    public static ArrayList<LocationHelper>location_lists_for_students;
    Button start;
    DatabaseReference firebase_refrences;
    FirebaseAuth firebaseAuth;
    DatabaseReference re;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_maps_for_teacher);
        FirebaseApp.initializeApp(this);

        maps_counter_goes++;
        start=findViewById(R.id.start_trip);
        firebaseAuth=FirebaseAuth.getInstance();

        trip_type=findViewById(R.id.trip_type);
        String []trips={"Morning trip, pickup Student","Evening trip, Drop off Student"};
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,trips);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        location_lists_for_students=new ArrayList<>();

        firebase_refrences=FirebaseDatabase.getInstance().getReference().child("all_buses").child(Location_area_for_teacher);
        firebase_refrences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bus_driver_uid_to_start_trip = (String) dataSnapshot.child("bus_driver_uid").getValue();
               // Toast.makeText(getApplicationContext(),bus_driver_uid_to_start_trip,Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),dataSnapshot.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        re=FirebaseDatabase.getInstance().getReference().child("all_district").child(Location_area_for_teacher);
        re.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(maps_counter_goes<2){
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    double a= Double.valueOf(dataSnapshot1.child("latitude").getValue().toString());
                    double b= Double.valueOf(dataSnapshot1.child("longitude").getValue().toString());
                    final String home_name=dataSnapshot1.child("child0").getValue().toString();
                    final LatLng latLng=new LatLng(a,b);
                 //   Toast.makeText(getApplicationContext(),dataSnapshot1.child("latitude").getValue().toString(),Toast.LENGTH_LONG).show();
                    final String Parrent_uid=dataSnapshot1.getKey().toString();
                    final LocationHelper attendanceHelper=new LocationHelper(latLng,home_name,Parrent_uid);
                    FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("BusTrip")
                            .child(dataSnapshot1.getKey().toString()).setValue(attendanceHelper);
                    FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("BusTrip")
                            .child(dataSnapshot1.getKey().toString()).child("visited").setValue(0);



                }}

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {

                    FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("BusTrip")
                            .child(dataSnapshot1.getKey().toString()).child("visited").setValue(0);


                }




            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        //Setting the ArrayAdapter data on the Spinner
        trip_type.setAdapter(aa);
        trip_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i)
                {
                    case 0:
                        trips_type_for_maps=1;
                        break;
                    case 1:
                        trips_type_for_maps=2;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if(is_checked_attend==0 &&trips_type_for_maps==2)
//                {
//                    Toast.makeText(getApplicationContext(),"Please Checks attendance first",Toast.LENGTH_LONG).show();
//
//                }else
//                    {
                FirebaseDatabase.getInstance().getReference().child(bus_driver_uid_to_start_trip).child("is_ready")
                        .setValue("go");
                if(trips_type_for_maps==2){
                startActivity(new Intent(getApplicationContext(),Attendance.class));
                finish();
                }else{
                    startActivity(new Intent(getApplicationContext(),RealTimeMap.class));
                    finish();
                }
            }

        });


    }
}
