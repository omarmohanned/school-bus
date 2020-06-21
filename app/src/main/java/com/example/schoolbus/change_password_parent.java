package com.example.schoolbus;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class change_password_parent extends AppCompatActivity {
    private EditText registered_emailid;
    private TextView forgot_button;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_parent);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        registered_emailid = findViewById(R.id.registered_emailid);
        forgot_button = findViewById(R.id.forgot_button);
        forgot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registered_emailid.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "you should enter a password", Toast.LENGTH_LONG).show();
                } else {
                    firebaseUser.updatePassword(registered_emailid.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "you have changes your password", Toast.LENGTH_LONG).show();
                                databaseReference.child(firebaseUser.getUid()).child("password").setValue(registered_emailid.getText().toString());

                            } else {
                                Toast.makeText(getApplicationContext(), "please try again", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        });
    }
}
