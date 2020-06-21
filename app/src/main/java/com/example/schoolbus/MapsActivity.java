package com.example.schoolbus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final String longitude = getIntent().getStringExtra("lon");
        final String latitude = getIntent().getStringExtra("lat");


        if (longitude==null || latitude==null) {
            Toast.makeText(getApplicationContext(), "we could not acquire your location", Toast.LENGTH_LONG).show();
            mMap.setMyLocationEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.setTrafficEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else {
            double a = Double.valueOf(longitude);
            double b = Double.valueOf(latitude);
            loc_sure(b,a);
            mMap.setBuildingsEnabled(true);
            mMap.setMyLocationEnabled(true);
            final LatLng place = new LatLng(b, a);
            mMap.addMarker(new MarkerOptions().position(place).title("Current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20.0f));
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.setTrafficEnabled(true);
            mMap.setBuildingsEnabled(true);

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    mMap.clear();
                    double lat = latLng.latitude;
                    double lan = latLng.longitude;
                    LatLng place2 = new LatLng(lat, lan);
                    mMap.addMarker(new MarkerOptions().position(place2).title("your exact Location"));
                    loc_sure(lat,lan);
                    Toast.makeText(getApplicationContext(), latitude + " \n" + longitude, Toast.LENGTH_LONG).show();


                }
            });
        }

    }

    private void loc_sure(final Double b, final Double a) {
        final String number = getIntent().getStringExtra("num");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder sure = new AlertDialog.Builder(MapsActivity.this);
                sure.setCancelable(false);
                sure.setTitle("location ");
                sure.setMessage("IS that your exact location??");

                sure.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final CheckBox checkBox2, checkBox;
                        Button button;
                        AlertDialog.Builder sleet44 = new AlertDialog.Builder(MapsActivity.this);
                        View view = getLayoutInflater().inflate(R.layout.last_step_parent, null);
                        checkBox = view.findViewById(R.id.checkBox);
                        checkBox2 = view.findViewById(R.id.checkBox2);
                        if (Integer.parseInt(number) <= 1) {

                            checkBox.setText("you have " + number + " child");
                        } else {
                            checkBox.setText("you have " + number + " children");
                        }

                        button = view.findViewById(R.id.button);
                        sleet44.setView(view);
                        sleet44.setCancelable(false);
                        sleet44.create().show();
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (checkBox.isChecked() && checkBox2.isChecked()) {
                                    ProgressDialog pd = new ProgressDialog(MapsActivity.this);
                                    pd.setMessage("finishing setup:" + "\n" + "please wait");
                                    pd.show();
                                    Handler handler1 = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            databaseReference.child(firebaseUser.getUid()).child("latitude").setValue(b);
                                            databaseReference.child(firebaseUser.getUid()).child("longitude").setValue(a);
                                            startActivity(new Intent(getApplicationContext(), main_parent.class));
                                            finish();
                                        }


                                    }, 7000);

                                }
                            }
                        });

                    }

                }).setNegativeButton("Not sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"long click on your exact location",Toast.LENGTH_LONG).show();

                    }
                }).create().show();
            }
        }, 5000);
    }


}
