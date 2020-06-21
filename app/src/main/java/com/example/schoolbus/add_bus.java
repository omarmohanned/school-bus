package com.example.schoolbus;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class add_bus extends AppCompatActivity {
    String bus_loc_tex;
    private Spinner bus_loc;
    private Button add_bus;
    private EditText bus_driver_name;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        bus_loc = findViewById(R.id.bus_loc);
        add_bus = findViewById(R.id.add_bus);
        bus_driver_name = findViewById(R.id.bus_driver_name);

        add_bus.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_checked, getResources().getStringArray(R.array.place_name));
        bus_loc.setAdapter(arrayAdapter);
        bus_loc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (bus_loc.getSelectedItem().toString().equals("choose") && bus_driver_name.getText().toString().isEmpty()) {
                    add_bus.setVisibility(View.INVISIBLE);
                } else {
                    add_bus.setVisibility(View.VISIBLE);
                    bus_loc_tex = bus_loc.getSelectedItem().toString();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        add_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                databaseReference.child("all_buses").child(bus_loc_tex).setValue(bus_loc_tex);
                databaseReference.child("all_buses_spinner").child(bus_loc_tex).setValue(bus_loc_tex);
                if (bus_driver_name.getText().toString().contains(" ")) {

                    Toast.makeText(getApplicationContext(), "it should be one name(no spaces )", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(bus_driver_name.getText().toString() + "@gmail.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                databaseReference.child("all_buses").child(bus_loc_tex).child("bus_driver").setValue(bus_driver_name.getText().toString());
                                databaseReference.child("all_buses").child(bus_loc_tex).child("bus_driver_uid").setValue(firebaseUser.getUid());
                                databaseReference.child(mAuth.getCurrentUser().getUid()).child("idintity").setValue("4");
                                databaseReference.child(firebaseUser.getUid()).child("uid").setValue(firebaseUser.getUid());
                                databaseReference.child(firebaseUser.getUid()).child("location_name").setValue(bus_loc_tex);
                                Snackbar.make(view,"Driver and bus added succesfully",Snackbar.LENGTH_LONG).show();

                            } else {


                            }
                        }
                    });
                }

            }
        });


    }
}
