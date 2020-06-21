package com.example.schoolbus;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.schoolbus.Attendance.*;
import com.example.schoolbus.routeBackEnd.FetchURL;
import com.example.schoolbus.routeBackEnd.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import static com.example.schoolbus.notfication_channel_for_parent_confirmation.*;
import static com.example.schoolbus.Start_maps_for_teacher.*;
import static com.example.schoolbus.Main_Teacher.*;

public class RealTimeMap extends AppCompatActivity implements OnMapReadyCallback,TaskLoadedCallback {

    GoogleMap my_map;


    FirebaseAuth firebaseAuth;
    DatabaseReference re;
    DatabaseReference change_location;
   LatLng school_destinations;
    TextView bus_speed,next_stop,estimated_time;
    Button stop;

    static String ui="";
    static String student_name="";
    ArrayList<LocationHelper>locations_lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_id);





        change_location=FirebaseDatabase.getInstance().getReference().child(teacher_user);

        school_destinations=new LatLng(32.03701,35.890452);

        mapFragment.getMapAsync(this);
        next_stop=findViewById(R.id.next_stop_for_bus);
        next_stop.setText(next_stop.getText().toString()+" ");
        bus_speed=findViewById(R.id.area_name_on_map);
        firebaseAuth=FirebaseAuth.getInstance();

        re=FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("BusTrip");




        estimated_time=findViewById(R.id.estimated_time_to_arrival);
        stop=findViewById(R.id.stop_tri);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child(bus_driver_uid_to_start_trip).child("is_ready")
                        .setValue("wait");

                startActivity(new Intent(getApplicationContext(),Main_Teacher.class));
                finish();
            }
        });


    }





    @Override
    public void onTaskDone(Object... values) {
        my_map.addPolyline((PolylineOptions) values[0]);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        my_map = googleMap;
        locations_lists =new ArrayList<LocationHelper>();
        my_map.setBuildingsEnabled(true);




        if(current_location==null) {
            Toast.makeText(getApplicationContext(), "we could not acquire your location", Toast.LENGTH_LONG).show();
        }
         else {




            change_location.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    locations_lists.clear();
                    my_map.clear();
                    double lo = (double)dataSnapshot.child("Location").child("latLng").child("longitude").getValue();
                    double la = (double)dataSnapshot.child("Location").child("latLng").child("latitude").getValue();
                    final LatLng current = new LatLng(la, lo);
//                    my_map.addMarker(new MarkerOptions().position(current).title("Bus").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_66)));

                    final Marker marker = my_map.addMarker(new MarkerOptions().position(current).title("Bus").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_66)));



                    re.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            marker.remove();
                            locations_lists.clear();
                            my_map.clear();




                            final Marker marker = my_map.addMarker(new MarkerOptions().position(current).title("Bus").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_66)));

                            my_map.addMarker(new MarkerOptions().position(school_destinations).title("school").icon(BitmapDescriptorFactory.fromResource(R.drawable.school)));




                            if(trips_type_for_maps==1){
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                {
                                    if(Integer.valueOf(dataSnapshot1.child("visited").getValue().toString())==0){
                                    double a= Double.valueOf(dataSnapshot1.child("latLng").child("latitude").getValue().toString());
                                    double b= Double.valueOf(dataSnapshot1.child("latLng").child("longitude").getValue().toString());
                                    final LatLng n=new LatLng(a,b);
                                    final String home_name=dataSnapshot1.child("home_name").getValue().toString();
                                    final String ParentUid=dataSnapshot1.getKey().toString();
                                    final LocationHelper locationHelper=new LocationHelper(n,home_name,ParentUid);

                                    locations_lists.add(locationHelper);
                                    }

                                }
                            }else
                            {

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                {

                                    if(dataSnapshot1.child("absent").getValue()!=null){
                                    if(dataSnapshot1.child("absent").getValue().toString()=="1")
                                        continue;
                                    else
                                        {
                                            if(Integer.valueOf(dataSnapshot1.child("visited").getValue().toString())==0){
                                        double a= Double.valueOf(dataSnapshot1.child("latLng").child("latitude").getValue().toString());
                                        double b= Double.valueOf(dataSnapshot1.child("latLng").child("longitude").getValue().toString());
                                        final LatLng n=new LatLng(a,b);
                                                final String ParentUid=dataSnapshot1.getKey().toString();
                                        final String home_name=dataSnapshot1.child("home_name").getValue().toString();
                                        final LocationHelper locationHelper=new LocationHelper(n,home_name,ParentUid);
                                        locations_lists.add(locationHelper);
                                        }
                                        }

                                }}

                            }
                            for(int i=0;i<locations_lists.size();i++)
                            {

                                my_map.addMarker(new MarkerOptions().position(locations_lists.get(i).getLatLng()).title(String.valueOf(locations_lists.get(i).getHome_name())).icon(BitmapDescriptorFactory.fromResource(R.drawable.home2)));



                            }



                            // het url for route between two locations
                            if(locations_lists.size()>1){
                             LatLng nearsest_locations= mIn(locations_lists,current);


                            String url=getUrl(current,nearsest_locations,"driving");
                            // fetch url to display location
                            new FetchURL(RealTimeMap.this).execute(url,"driving");

                            float[] des = new float[1];
                            Location.distanceBetween(current.latitude, current.longitude,
                                    nearsest_locations.latitude, nearsest_locations.longitude, des);

                            des[0]=des[0]/100;


                                send_notify_if_nearby(current,nearsest_locations,ui,student_name);
                            int estimatedDriveTimeInMinutes;
                            if(bus_speed_in_km_h==0)
                            {
                                estimatedDriveTimeInMinutes = (int) (des[0] / 1);
                            }else
                            {
                                estimatedDriveTimeInMinutes = (int) (des[0] / bus_speed_in_km_h);
                            }


                            bus_speed.setText("Bus speed: "+" "+bus_speed_in_km_h+" km/h");
                                FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("Bus speed")
                                        .setValue(bus_speed_in_km_h+"Km/h");




                            my_map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            my_map.setTrafficEnabled(true);
                            my_map.animateCamera( CameraUpdateFactory.newLatLngZoom(current,12.0f));
                            my_map.setBuildingsEnabled(true);
                            if(estimatedDriveTimeInMinutes>60)
                                estimated_time.setText("Time to get next stop: "+estimatedDriveTimeInMinutes/60+ " hours");
                            else
                                estimated_time.setText("Time to get next stop: "+estimatedDriveTimeInMinutes+ " minutes");
                                FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("Time to next stop")
                                        .setValue(estimatedDriveTimeInMinutes+ " minutes");



                            //  my_map.setMyLocationEnabled(true);
                        }}

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });




                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });





}}

void send_notify_if_nearby(LatLng current,LatLng next,String ui,String name)
{
    float[] results = new float[1];
    Location.distanceBetween(current.latitude, current.longitude,
            next.latitude, next.longitude, results);


//    if(results[0]<=400 && results[0]>250){
//
//
//        FirebaseDatabase.getInstance().getReference().child(ui).child("New_Notification")
//                .child("Notify from teacher").setValue("The Bus is nearby");
//
//
//    }

    if(results[0]<=50){


        final boolean[] c = {true};
        final String UU=ui;
        FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("BusTrip")
                .child(UU).child("visited").setValue(1);
        AlertDialog alertDialog = new AlertDialog.Builder(RealTimeMap.this).create();
        alertDialog.setTitle("Confirm Student Aِِِttendance");
        alertDialog.setMessage("Is "+ name +" attended to the bus?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("BusTrip")
                                .child(UU).child("absent").setValue("0");
                        dialog.dismiss();



                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child(UU).child("New_Notification")
                                .child("Notify from teacher").setValue("is absencent");
                        FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("BusTrip")
                                .child(UU).child("absent").setValue("1");
                        dialog.dismiss();


                    }
                });
        try {
            alertDialog.show();
        }
        catch (WindowManager.BadTokenException e) {
            //use a log message
        }










    }


}

LatLng mIn(ArrayList <LocationHelper>locations,LatLng cur)
{
    float[] results = new float[1];
    int j=1;
    Location.distanceBetween(cur.latitude, cur.longitude,
            locations.get(0).getLatLng().latitude, locations.get(0).getLatLng().longitude, results);

    for(int i=0;i<locations.size();i++)
    {


                float[] new_result = new float[1];
                Location.distanceBetween(locations.get(i).getLatLng().latitude, locations.get(i).getLatLng().longitude,
                        cur.latitude, cur.longitude, new_result);
                if(new_result[0]<=results[0]) {
                    results = new_result;
                    j=i;
                }

    }

//    Toast.makeText(getApplicationContext(), String.valueOf(results[0]/1000),Toast.LENGTH_LONG).show();

    ui=locations.get(j).getParen_uid();
    student_name=locations.get(j).getHome_name();
    next_stop.setText("Next stop: "+locations.get(j).getHome_name()+" home");
    FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("Next stop")
            .setValue(locations.get(j).getHome_name());

    return locations.get(j).getLatLng();
}

}
