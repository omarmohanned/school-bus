package com.example.schoolbus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class parent extends AppCompatActivity {
    final String[] cars = new String[50];
    int a = 0;
    DatabaseReference databaseReference, getDatabaseReference1;
    private Button finish, map;
    private LinearLayout layout;
    private final List<String> areas = new ArrayList<>();
    private FloatingActionButton floatingActionButton;
    private int num_of_clicks = 0;
    private int num_of_confirm = 0;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private String lon1, lat1;
    private FirebaseUser firebaseUser;
    private Spinner place_name;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        layout = findViewById(R.id.layout);
        location();
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setVisibility(View.INVISIBLE);
        place_name = findViewById(R.id.place_name);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getDatabaseReference1 = FirebaseDatabase.getInstance().getReference().child("all_buses_spinner");
        getDatabaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    areas.add(ds.getValue(String.class));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_checked, areas);
                place_name.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        place_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (place_name.getSelectedItem().toString().equals(null)) {
                    floatingActionButton.setVisibility(View.INVISIBLE);


                } else {
                    floatingActionButton.setVisibility(View.VISIBLE);
                    //databaseReference.child(firebaseUser.getUid()).child("district").setValue(place_name.getSelectedItem().toString());//good
                   // databaseReference.child("all_district").child(place_name.getSelectedItem().toString()).setValue(place_name.getSelectedItem().toString());//

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num_of_clicks++;
                final EditText name;
                final FloatingActionButton add;
               // databaseReference.child(firebaseUser.getUid()).child("uid").setValue(firebaseUser.getUid());//data base
                Snackbar.make(view, "Enter the " + num_of_clicks + " child name", Snackbar.LENGTH_LONG).show();
                View view1 = getLayoutInflater().inflate(R.layout.children_input_parent, null);
                name = view1.findViewById(R.id.name);
                name.setHint("enter child number " + num_of_clicks);
                add = view1.findViewById(R.id.add);
                layout.addView(view1);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final AlertDialog.Builder add1 = new AlertDialog.Builder(parent.this);
                        add1.setMessage("If you tap yes you will not be able to change the name in the future");
                        add1.setTitle("check name");
                        add1.setIcon(getResources().getDrawable(R.drawable.ic_check_circle_black_24dp));
                        add1.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {

                                if (name.getText().toString().isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Empty name ", Toast.LENGTH_LONG).show();
                                } else if (name.getText().toString().contains(".") || name.getText().toString().contains("#") || name.getText().toString().contains("$") || name.getText().toString().contains("[") || name.getText().toString().contains("]")) {
                                    Toast.makeText(getApplicationContext(), "must not contain '.', '#', '$', '[', or ']'  ", Toast.LENGTH_LONG).show();

                                } else {
                                    Snackbar.make(view, "This name is now registered :" + name.getText().toString(), Snackbar.LENGTH_LONG).show();

                                    databaseReference.child(firebaseUser.getUid()).child("children").child("child" + num_of_confirm).setValue(name.getText().toString());//data base
                                    databaseReference.child("all_district").child(place_name.getSelectedItem().toString()).child(firebaseUser.getUid()).child("child" + num_of_confirm).setValue(name.getText().toString());//data base
                                    databaseReference.child("all_district").child(place_name.getSelectedItem().toString()).child(firebaseUser.getUid()).child("latitude").setValue(lat1);//data base
                                    databaseReference.child("all_district").child(place_name.getSelectedItem().toString()).child(firebaseUser.getUid()).child("longitude").setValue(lon1);//data base


                                    cars[a] = name.getText().toString();
                                    a++;
                                    num_of_confirm++;
                                    name.setBackgroundColor(getResources().getColor(R.color.bac));
                                    name.setEnabled(false);
                                    View view2 = getLayoutInflater().inflate(R.layout.finish_from_parent_to_maps, null);
                                    finish = view2.findViewById(R.id.finish);
                                    map = view2.findViewById(R.id.map);
                                    map.setVisibility(View.INVISIBLE);
                                    if (num_of_clicks == num_of_confirm) {

                                        layout.addView(view2);
                                    }
                                    finish.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View view) {
                                            AlertDialog.Builder alert = new AlertDialog.Builder(parent.this);
                                            alert.setCancelable(false);
                                            alert.setIcon(getResources().getDrawable(R.drawable.ic_check_circle_black_24dp));
                                            alert.setTitle("checking");
                                            alert.setMessage("Are you sure these are your children");
                                            alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                @SuppressLint("RestrictedApi")
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    location();
                                                    for (int b = 0; b <= a - 1; b++) {
                                                        Toast.makeText(getApplicationContext(), cars[b], Toast.LENGTH_LONG).show();
                                                    }
                                                    floatingActionButton.setVisibility(View.INVISIBLE);
                                                    finish.setVisibility(View.INVISIBLE);
                                                    map.setVisibility(View.VISIBLE);

                                                }
                                            }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {


                                                }
                                            });
                                            alert.create().show();

                                        }
                                    });
                                    map.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final AlertDialog.Builder get_location = new AlertDialog.Builder(parent.this);
                                            View view3 = getLayoutInflater().inflate(R.layout.get_location_view, null);
                                            final ProgressBar progressBar2;
                                            progressBar2 = view3.findViewById(R.id.progressBar2);
                                            get_location.setView(view3);
                                            get_location.setCancelable(true);
                                            get_location.setCancelable(false);
                                            get_location.create().show();
                                            CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
                                                @Override
                                                public void onTick(long l) {
                                                    final int[] a = {33};
                                                    progressBar2.setProgress(a[0]);
                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            a[0] = a[0] + (100 / 3);
                                                            progressBar2.setProgress(a[0]);
                                                        }
                                                    }, 300);
                                                    location();
                                                }

                                                @Override
                                                public void onFinish() {
                                                    dialogInterface.dismiss();
                                                    View view4 = getLayoutInflater().inflate(R.layout.location_final_aqqu, null);
                                                    get_location.setView(view4);
                                                    get_location.create().show();
                                                    dialogInterface.dismiss();
                                                    final Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            final Intent maps = new Intent(getApplicationContext(), MapsActivity.class);
                                                            maps.putExtra("lon", lon1);
                                                            maps.putExtra("lat", lat1);
                                                            String n1 = String.valueOf(num_of_clicks);
                                                            maps.putExtra("num", n1);
                                                            databaseReference.child(firebaseUser.getUid()).child("latitude").setValue(lat1);//input data base
                                                            databaseReference.child(firebaseUser.getUid()).child("longitude").setValue(lon1);//input data base
                                                            databaseReference.child(firebaseUser.getUid()).child("Location").child("latLng").child("longitude").setValue(lon1);
                                                            databaseReference.child(firebaseUser.getUid()).child("Location").child("latLng").child("latitude").setValue(lat1);
                                                            databaseReference.child("all_district").child(place_name.getSelectedItem().toString()).child(firebaseUser.getUid()).child("latitude").setValue(lat1);//
                                                            databaseReference.child("all_district").child(place_name.getSelectedItem().toString()).child(firebaseUser.getUid()).child("longitude").setValue(lon1);//


                                                            databaseReference.child(firebaseUser.getUid()).child("perm_latitude").setValue(Double.valueOf(lat1));//input data base
                                                            databaseReference.child(firebaseUser.getUid()).child("perm_longitude").setValue(Double.valueOf(lon1));//input data base
                                                            databaseReference.child(firebaseUser.getUid()).child("idintity").setValue("5");
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    startActivity(maps);
                                                                    finish();
                                                                }
                                                            }, 3000);


                                                        }
                                                    }, 2000);
                                                }
                                            }.start();
                                        }
                                    });
                                }

                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();


                    }
                });


            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        } else {

                            locationManager.requestLocationUpdates("gps", 1000, 5, locationListener);
                        }
                    }

                }

        }
    }

    private void location() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
              //  Toast.makeText(getApplicationContext(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
                lon1 = String.valueOf(location.getLongitude());
                lat1 = String.valueOf(location.getLatitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
                        return;
                    } else {
                        locationManager.requestLocationUpdates("gps", 1000, 5, locationListener);
            }
        }
    }

}
