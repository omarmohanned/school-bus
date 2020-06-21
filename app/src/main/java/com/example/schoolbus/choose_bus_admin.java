package com.example.schoolbus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class choose_bus_admin extends AppCompatActivity {
    private final List<String> areas = new ArrayList<>();
    private Spinner bus_location;
    private Button show_bus;
    private DatabaseReference databaseReference, databaseReference1;
    private String teacher_uid,lat,lon;
    public static String Bus_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bus_admin);
        bus_location = findViewById(R.id.bus_location);
        show_bus = findViewById(R.id.show_bus);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("all_buses_spinner");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    areas.add(ds.getValue(String.class));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_checked, areas);
                bus_location.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        show_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bus_name=bus_location.getSelectedItem().toString();
                databaseReference1 = FirebaseDatabase.getInstance().getReference().child("all_buses").child(bus_location.getSelectedItem().toString()).child("teacher");
                databaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        teacher_uid = dataSnapshot.getValue(String.class);
                        if (teacher_uid==null) {
                            Toast.makeText(getApplicationContext(), "No teacher assigned to this bus", Toast.LENGTH_LONG).show();

                        } else {
                            Intent send_info=new Intent(getApplicationContext(),maps_for_admin.class);
                            send_info.putExtra("uid",teacher_uid);
                            startActivity(send_info);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
