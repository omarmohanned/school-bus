package com.example.schoolbus;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.schoolbus.Main_Teacher.current_location;
import static com.example.schoolbus.driver_main.*;

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

import java.util.ArrayList;

import static com.example.schoolbus.Main_Teacher.Location_area_for_teacher;
import static com.example.schoolbus.Main_Teacher.teacher_user;

public class Maps_Driver extends FragmentActivity implements OnMapReadyCallback,TaskLoadedCallback {

    GoogleMap my_map;

    String nex_sttttooopp;
    FirebaseAuth firebaseAuth;
    DatabaseReference database_for_bus_location;

    DatabaseReference change_location;
    LatLng school_destinations;
   static String locations_name_;
    Button stop;
    String teacher_uid;
    TextView Next_st;

    DatabaseReference teacher_reference;

    ArrayList<LocationHelper> locations_lists ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps__driver);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_driver);
        mapFragment.getMapAsync(this);
        firebaseAuth=FirebaseAuth.getInstance();

        locations_lists=new ArrayList<LocationHelper>();
        database_for_bus_location=FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid());
        Next_st=findViewById(R.id.next_stop_for_driver);


        school_destinations=new LatLng(32.03701,35.890452);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        my_map = googleMap;





            database_for_bus_location = FirebaseDatabase.getInstance().getReference().child(Teacher_Uid).child("BusTrip");
            teacher_reference=FirebaseDatabase.getInstance().getReference().child(Teacher_Uid).child("Location");
            teacher_reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    locations_lists.clear();
                    my_map.clear();
                    double lo = (double)dataSnapshot.child("latLng").child("longitude").getValue();
                    double la = (double)dataSnapshot.child("latLng").child("latitude").getValue();
                    final LatLng current = new LatLng(la, lo);


                    database_for_bus_location.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            locations_lists.clear();
                            my_map.clear();



                            final Marker marker = my_map.addMarker(new MarkerOptions().position(current).title("Bus").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_66)));

                            my_map.addMarker(new MarkerOptions().position(school_destinations).title("school").icon(BitmapDescriptorFactory.fromResource(R.drawable.school)));


                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                double a= Double.valueOf(dataSnapshot1.child("latLng").child("latitude").getValue().toString());
                                double b= Double.valueOf(dataSnapshot1.child("latLng").child("longitude").getValue().toString());
                                final String home_name=dataSnapshot1.child("home_name").getValue().toString();
                                final LatLng n=new LatLng(a,b);



                                final LocationHelper locationHelper = new LocationHelper(n, home_name,dataSnapshot1.getKey().toString());
                                locations_lists.add(locationHelper);


                                //my_map.addMarker(new MarkerOptions().position(n).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));
                            }
                            for (int i = 0; i < locations_lists.size(); i++) {

                                my_map.addMarker(new MarkerOptions().position(locations_lists.get(i).getLatLng()).title(String.valueOf(locations_lists.get(i).getHome_name())).icon(BitmapDescriptorFactory.fromResource(R.drawable.home2)));


                            }
                            LatLng nearsest_locations= mIn(locations_lists);
                            String url=getUrl(current,nearsest_locations,"driving");
                            // fetch url to display location
                            new FetchURL(Maps_Driver.this).execute(url,"driving");


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                    my_map.animateCamera( CameraUpdateFactory.newLatLngZoom(current,12.0f));













                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        teacher_reference=FirebaseDatabase.getInstance().getReference().child(Teacher_Uid).child("Next stop");
        teacher_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Next_st.setText("Next Stop: "+dataSnapshot.getValue(String.class)+" Home");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







        }


    LatLng mIn(ArrayList <LocationHelper>locations)
    {
        float[] results = new float[1];
        int j=1;
        Location.distanceBetween(locations.get(0).getLatLng().latitude, locations.get(0).getLatLng().longitude,
                locations.get(1).getLatLng().latitude, locations.get(1).getLatLng().longitude, results);

        for(int i=2;i<locations.size();i++)
        {


            float[] new_result = new float[1];
            Location.distanceBetween(locations.get(0).getLatLng().latitude, locations.get(0).getLatLng().longitude,
                    locations.get(i).getLatLng().latitude, locations.get(i).getLatLng().longitude, new_result);
            if(new_result[0]<=results[0]) {
                results = new_result;
                j=i;
            }

        }

        return locations.get(j).getLatLng();
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

}
