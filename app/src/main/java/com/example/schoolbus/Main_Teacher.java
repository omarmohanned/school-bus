package com.example.schoolbus;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schoolbus.routeBackEnd.FetchURL;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Main_Teacher extends Activity {

    TextView clockView;
    String notification_message;
    DatabaseReference firebase_refrences;
    Student student;
    Button Button_maps,log_out,notification_to_all;
    Button Attendance_intity;
    ImageButton teacher_profile_b;
    ImageButton call_emergancy;
    TextView view;
    public static int bus_speed_in_km_h;
    private Handler handler = new Handler();
    public LocationListener locationListener;
    public LocationManager locationManager;
    public static LatLng current_location;
    public static String teacher_user;
    FirebaseAuth firebaseAuth;
    public static String Location_area_for_teacher="";

    String currentDate;

    private ProgressBar progressBar;
    private int progressStatus = 0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__teacher);
        FirebaseApp.initializeApp(this);

        call_emergancy=findViewById(R.id.emergency);
        notification_to_all=findViewById(R.id.send_notification_to_all);
        teacher_profile_b=findViewById(R.id.teacher_profile);
        teacher_profile_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Teacher_Profile_Page.class);
                startActivity(intent);
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        log_out=findViewById(R.id.log_out_teacher);
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.getCurrentUser().getUid();
                Intent intent=new Intent(Main_Teacher.this,MainActivity.class);
                startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();


            }
        });


        firebase_refrences=FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid());
        firebase_refrences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Location_area_for_teacher=dataSnapshot.child("location_name").getValue().toString();
                if(dataSnapshot.child("Location").child("latLng").getValue()!=null) {
                    double lat = (double) dataSnapshot.child("Location").child("latLng").child("latitude").getValue();
                    double lan = (double) dataSnapshot.child("Location").child("latLng").child("longitude").getValue();
                    current_location = new LatLng(lat, lan);
                }



               // Toast.makeText(getApplicationContext(),"Bus area:"+Location_area_for_teacher,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


       // Toast.makeText(getApplicationContext(),"Teacher location: "+Location_area_for_teacher,Toast.LENGTH_LONG).show();
        firebase_refrences=FirebaseDatabase.getInstance().getReference().child("all_district").child(Location_area_for_teacher);
        final ArrayList<String>parents_uid=new ArrayList<>();
        firebase_refrences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {

                    parents_uid.add(dataSnapshot1.getKey()
                            .toString());
                   // Toast.makeText(getApplicationContext(),"Parent uid:"+dataSnapshot1.getKey().toString(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








        teacher_user=firebaseAuth.getCurrentUser().getUid();

        clockView = findViewById(R.id.clock_view);

        Button_maps=findViewById(R.id.button_maps);
        location();
        Button_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar = findViewById(R.id.Pogress_waiting);
                progressBar.setVisibility(View.VISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Intent maps = new Intent(Main_Teacher.this, Start_maps_for_teacher.class);
                        startActivity(maps);

                        progressBar.setVisibility(View.INVISIBLE);


                    }}, 3000);


            }
        });





        notification_to_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Main_Teacher.this,R.style.MyDialogTheme);
                builder.setTitle("Send notification");
                final EditText input = new EditText(getApplicationContext());

                currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                input.setHeight(200);
                builder.setView(input);


                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notification_message = currentDate+"\n"+input.getText().toString();

                        for(int i=0;i<parents_uid.size();i++)
                        {

                            Date dNow = new Date();
                            SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                            String datetime = ft.format(dNow);
                            FirebaseDatabase.getInstance().getReference().child(parents_uid.get(i)).child("notfication_History")
                                    .child(datetime).setValue(notification_message);
                            FirebaseDatabase.getInstance().getReference().child(parents_uid.get(i)).child("New_Notification")
                                   .child("Notify from teacher").setValue(notification_message);
                            Toast.makeText(getApplicationContext(),"notification sent successfully",Toast.LENGTH_LONG).show();

                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });



        call_emergancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:911"));
                    startActivity(intent);
                } else {
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"));
                    startActivity(intent);
                }
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

                            locationManager.requestLocationUpdates("gps", 1000, 3, locationListener);
                        }
                    }

                }

        }
    }
//////////////////////////////////////////////
    void location() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Toast.makeText(getApplicationContext(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
                current_location=new LatLng(location.getLatitude(),location.getLongitude());
                LocationHelper locationHelper=new LocationHelper(current_location," ","");
                FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("Location")
                        .setValue(locationHelper);
                if (location==null){
                    // if you can't get speed because reasons :)
                    bus_speed_in_km_h=0;
                }
                else{
                    //int speed=(int) ((location.getSpeed()) is the standard which returns meters per second. In this example i converted it to kilometers per hour

                    int speed=(int) ((location.getSpeed()*3600)/1000);

                    bus_speed_in_km_h=speed;
                }


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
                locationManager.requestLocationUpdates("gps", 1000, 3, locationListener);
            }
        }
    }






}

