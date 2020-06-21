package com.example.schoolbus;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import static com.example.schoolbus.choose_bus_admin.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class maps_for_admin extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String teacher_uid_get, lat, lon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_for_admin);
        teacher_uid_get = getIntent().getStringExtra("uid");

        FirebaseDatabase.getInstance().getReference().child(teacher_uid_get).child("Location").child("latLng")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        lat = dataSnapshot.child("latitude").getValue().toString();
                        lon = dataSnapshot.child("longitude").getValue().toString();

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void check_location(Double lat, Double lon) {

        if (lat==null && lon==null) {
            LatLng place = new LatLng(4, 5);
            mMap.addMarker(new MarkerOptions().position(place).title("sorry location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
            //mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.5f));
        } else {
            LatLng place = new LatLng(lon, lat);

            mMap.addMarker(new MarkerOptions().position(place).title(Bus_name).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_66)));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.5f));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                check_location(Double.valueOf(lon), Double.valueOf(lat));
            }
        },1500);

    }
}