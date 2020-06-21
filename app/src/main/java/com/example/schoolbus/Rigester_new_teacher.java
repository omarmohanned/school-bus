package com.example.schoolbus;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Rigester_new_teacher extends AppCompatActivity {

    private final List<String> areas = new ArrayList<>();
    Button save_change;
    ProgressBar progressBar;
    private EditText teacher_email;
    private Spinner location;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_new_teacher);
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.waiting_bar_for_signup);
        teacher_email = findViewById(R.id.email_for_teacher_new_registerd);
        location = findViewById(R.id.location);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("all_buses_spinner");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    areas.add(ds.getValue(String.class));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_checked, areas);
                location.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        save_change = findViewById(R.id.save_teacher_information);
        save_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(teacher_email.getText().toString(), "123456")
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("idintity")
                                            .setValue("1");
                                    FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("password")
                                            .setValue("123456");
                                    FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("location_name")
                                            .setValue(location.getSelectedItem().toString());
                                    FirebaseDatabase.getInstance().getReference().child("all_buses").child(location.getSelectedItem().toString()).child("start_trip").setValue(0);

                                    FirebaseDatabase.getInstance().getReference().child("all_buses").child(location.getSelectedItem().toString()).child("teacher")
                                            .setValue(mAuth.getCurrentUser().getUid());

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();

                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

    }
}
