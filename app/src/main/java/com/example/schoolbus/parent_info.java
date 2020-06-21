package com.example.schoolbus;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.schoolbus.notfication_channel_for_parent_confirmation.channel1;


public class parent_info extends Fragment implements OnMapReadyCallback {

    public Geocoder geocoder;
    List<Address> addresses;
    String fulladdress, loc_name, uid_teacher, next_stop, time_next_stop,name;
    Double lat = 0.0, lon = 0.0, lat_te = 0.0, lon_te = 0.0, perm_latt, perm_longg;
    String confirmation_code;
    Long saved_time = System.currentTimeMillis();
    private TextView textView7;
    private GoogleMap mMap;
    private Button change_location, bus_location, my_location, change_pass, log_out;
    private DatabaseReference databaseReference, get_loc_name, get_uid_teacher, databaseReference2, databaseReference_loc_name, databaseReference3, databaseReference4, databaseReference_mylocation_lat, databaseReference_mylocation_lon, databaseReference_loc_tea, perm_lat, perm_long, databaseReference_not, databaseReference_info;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private OnFragmentInteractionListener mListener;
    private double b1;
    private double a1;


    public parent_info() {
        // Required empty public constructor
    }

    public static parent_info newInstance(String param1, String param2) {
        parent_info fragment = new parent_info();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference4 = FirebaseDatabase.getInstance().getReference();


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_parent_info, container, false);


    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        final NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getActivity());
        textView7 = view.findViewById(R.id.textView7);
        bus_location = view.findViewById(R.id.bus_location);
        my_location = view.findViewById(R.id.my_location);
        change_pass = view.findViewById(R.id.change_pass);
        log_out = view.findViewById(R.id.log_out);
        ///////////////////////////////////////////////////////////////////////////
        perm_lat = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child("perm_latitude");
        perm_long = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child("perm_longitude");


        ///////////////////////////////////////////////////////////////////////////////////////
        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), change_password_parent.class));
            }
        });
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), MainActivity.class));

            }
        });


        Toast.makeText(getContext(), "started calendar", Toast.LENGTH_LONG).show();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 35);
        calendar.set(Calendar.SECOND, 13);

        Intent intent = new Intent(getContext(), Nofication_reciver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


///////////////////////////////////////////////////////////////////////////
        databaseReference_not = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child("New_Notification").child("Notify from teacher");
        databaseReference_not.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String notfication_te = dataSnapshot.getValue(String.class);
                if (notfication_te == null) {
                } else {
                    Notification notification = new NotificationCompat.Builder(getContext(), channel1).setSmallIcon(R.drawable.bus_icon)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true).setContentTitle("Teacher notification").setColor(3)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE).setContentText(dataSnapshot.getValue(String.class)).build();
                    databaseReference_not.setValue(null);
                    managerCompat.notify(1, notification);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        databaseReference_mylocation_lat = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child("latitude");
        databaseReference_mylocation_lon = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child("longitude");

        databaseReference_mylocation_lat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lat = dataSnapshot.getValue(Double.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        databaseReference_mylocation_lon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lon = dataSnapshot.getValue(Double.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final AlertDialog.Builder conf = new AlertDialog.Builder(getContext());
        conf.setCancelable(false);
        View conf1 = getLayoutInflater().inflate(R.layout.confrimation_code, null, false);
        conf.setView(conf1);
        final TextView textView1 = conf1.findViewById(R.id.conf);


        change_location = view.findViewById(R.id.change_location);

        Intent intent1 = new Intent(view.getContext(), tab_settings.class);
        final PendingIntent pendingIntent1t = PendingIntent.getActivity(view.getContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        geocoder = new Geocoder(view.getContext(), Locale.getDefault());
        change_location.setClickable(false);

        change_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference3 = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child("password");
                databaseReference3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String pass = dataSnapshot.getValue(String.class);
                        confirmation_code = pass;


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (a1 == 0) {

                    Toast.makeText(getContext(), "please choose your location \n by long press on wanted location", Toast.LENGTH_LONG).show();
                } else {
                    conf.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {

                            if (textView1.getText().toString().equals(confirmation_code)) {//here
                                databaseReference2 = FirebaseDatabase.getInstance().getReference();
                                databaseReference2.child(firebaseUser.getUid()).child("latitude").setValue(b1);
                                databaseReference2.child(firebaseUser.getUid()).child("longitude").setValue(a1);
                                //////////////////////////////////////////////////////////////////////////////////
                                databaseReference_loc_name = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child("district");
                                databaseReference_loc_name.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                         name = dataSnapshot.getValue(String.class);
                                        databaseReference_loc_name=FirebaseDatabase.getInstance().getReference();
                                        databaseReference_loc_name.child("all_district").child(name).child(firebaseUser.getUid()).child("latitude").setValue(b1);
                                        databaseReference_loc_name.child("all_district").child(name).child(firebaseUser.getUid()).child("longitude").setValue(a1);


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                try {
                                    addresses = geocoder.getFromLocation(b1, a1, 1);

                                    String address = addresses.get(0).getAddressLine(0);
                                    String area = addresses.get(0).getLocality();
                                    String city = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalcode = addresses.get(0).getPostalCode();

                                    fulladdress = address + "," + area + "," + city + ",";
                                    textView7.setText(fulladdress);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Notification notification = new NotificationCompat.Builder(getActivity(), channel1).setSmallIcon(R.drawable.bus_icon)
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setAutoCancel(true).setContentIntent(pendingIntent1t).setContentTitle("LOCATION APPROVED").setColor(3)
                                                .setCategory(NotificationCompat.CATEGORY_MESSAGE).setContentText("the new location is" + " :" + fulladdress).build();

                                        managerCompat.notify(1, notification);
                                        String date = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault()).format(new Date());
                                        databaseReference4.child(firebaseUser.getUid()).child("notfication_History").child(String.valueOf(System.currentTimeMillis())).setValue(date + "\n" + "the location is approved to :" + "\n" + fulladdress + "\n");
                                    }
                                }, 5000);

                            } else if (textView1.getText().toString().isEmpty()) {
                                Snackbar.make(getView(), "Enter the code", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(getView(), "That is not the write code", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }).setNegativeButton("ABORT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setIcon(R.drawable.bus_icon).create().show();


                }


            }


        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

////////////////////////////
                check_location(lat, lon);
            }
        });


        bus_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                get_loc_name = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child("district");
                get_loc_name.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        loc_name = dataSnapshot.getValue(String.class);


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (loc_name == null) {
                                    Toast.makeText(getContext(), "there is no teacher assigned to this bus./n" +
                                            "please contact the supervisor", Toast.LENGTH_LONG).show();
                                } else {
                                    get_uid_teacher = FirebaseDatabase.getInstance().getReference().child("all_buses").child(loc_name).child("teacher");

                                    get_uid_teacher.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            uid_teacher = dataSnapshot.getValue(String.class);
                                            Snackbar.make(view, uid_teacher, Snackbar.LENGTH_LONG).show();
                                            databaseReference_info = FirebaseDatabase.getInstance().getReference().child(uid_teacher).child("Next stop");
                                            databaseReference_info.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    next_stop = dataSnapshot.getValue(String.class);

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            databaseReference_info = FirebaseDatabase.getInstance().getReference().child(uid_teacher).child("Time to next stop");
                                            databaseReference_info.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    time_next_stop = dataSnapshot.getValue(String.class);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            if (uid_teacher == null) {
                                                Toast.makeText(getContext(), "please try again", Toast.LENGTH_LONG).show();
                                            }
                                            databaseReference_loc_tea = FirebaseDatabase.getInstance().getReference().child(uid_teacher).child("Location").child("latLng");
                                            databaseReference_loc_tea.child("latitude").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    lat_te = dataSnapshot.getValue(Double.class);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            databaseReference_loc_tea.child("longitude").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    lon_te = dataSnapshot.getValue(Double.class);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            check_location(lat_te, lon_te);
                                            Snackbar.make(view, "the next stop will be: " + " " + next_stop + " " + "and the bus will arrive in: " + " " + time_next_stop + " " + "", Snackbar.LENGTH_LONG).show();

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }, 1500);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    private void check_location(Double lat, Double lon) {
        mMap.clear();
        if (lat.equals(0.0) && lon.equals(0.0)) {
            LatLng place = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(place).title("Current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.5f));
        } else {


            LatLng place = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(place).title("Current location").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_66)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.5f));
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    mMap.clear();
                    b1 = latLng.latitude;
                    a1 = latLng.longitude;
                    mMap.setTrafficEnabled(true);
                    mMap.setBuildingsEnabled(true);
                    Toast.makeText(getContext(), "you can know press the location button", Toast.LENGTH_LONG).show();
                    LatLng place1 = new LatLng(b1, a1);
                    mMap.addMarker(new MarkerOptions().position(place1).title("Current location"));
                    change_location.setClickable(true);

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        perm_lat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                perm_latt = dataSnapshot.getValue(Double.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        perm_long.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                perm_longg = dataSnapshot.getValue(Double.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (System.currentTimeMillis() >= saved_time + 24 * 60 * 60 * 1000) {
            Toast.makeText(getContext(), "it has been 24 hours the location will be changed to default", Toast.LENGTH_LONG).show();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(firebaseUser.getUid()).child("latitude").setValue(perm_latt);
            databaseReference.child(firebaseUser.getUid()).child("longitude").setValue(perm_longg);


        }
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
