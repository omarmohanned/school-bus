package com.example.schoolbus;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText email_sign_in, password_sign_in;
    private Button sign_in;
    private TextView sign_up;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private  String start_lat;
    private String start_lon;
    private FloatingActionButton settings;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference, reference1;
    private FirebaseUser firebaseUser;
    private String know, id;
    private DatabaseReference databaseReference;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        email_sign_in = findViewById(R.id.email_sign_in);
        location();
        password_sign_in = findViewById(R.id.password_sign_in);
        sign_in = findViewById(R.id.sign_in);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email_sign_in.getText().toString().trim().isEmpty() || password_sign_in.getText().toString().trim().isEmpty()) {

                    Toast.makeText(getApplicationContext(), "please fill email or password", Toast.LENGTH_LONG).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email_sign_in.getText().toString(), password_sign_in.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "signing in", Toast.LENGTH_LONG).show();
                                        final Dialog dialog = new Dialog(MainActivity.this);
                                        dialog.setContentView(R.layout.loading_layout);
                                        dialog.show();
                                        CountDownTimer countDownTimer = new CountDownTimer(5000, 100) {
                                            @Override
                                            public void onTick(long l) {

                                            }

                                            @Override
                                            public void onFinish() {

                                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        id = dataSnapshot.child(firebaseUser.getUid()).child("idintity").getValue(String.class);

                                                        if (id.equals("1")) {
                                                            startActivity(new Intent(getApplicationContext(), Main_Teacher.class));
                                                            finish();
                                                        } else if (id.equals("2")) {
                                                            final Intent parent = new Intent(getApplicationContext(), parent.class);
                                                            parent.putExtra("lon_start", start_lat);
                                                            parent.putExtra("lat_start", start_lon);

                                                            startActivity(parent);
                                                            parent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            parent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            finish();
                                                        } else if (id.equals("3")) {
                                                            startActivity(new Intent(getApplicationContext(), admin.class));
                                                            finish();

                                                        } else if (id.equals("4")) {
                                                            startActivity(new Intent(getApplicationContext(), driver_main.class));
                                                            finish();
                                                        } else if (id.equals("5")) {
                                                            startActivity(new Intent(getApplicationContext(), main_parent.class));
                                                            finish();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                        }.start();
                                    } else {
                                        String problem = task.getException().getMessage().substring(30);
                                        Toast.makeText(getApplicationContext(), "                  failed to log in\n" + problem, Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                }

            }
        });
    }

    private void location() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
               // Toast.makeText(getApplicationContext(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
                start_lon = String.valueOf(location.getLongitude());
                start_lat = String.valueOf(location.getLatitude());
               // Toast.makeText(getApplicationContext(),start_lat,Toast.LENGTH_LONG).show();


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

