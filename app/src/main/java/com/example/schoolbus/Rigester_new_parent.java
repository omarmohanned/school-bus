package com.example.schoolbus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolbus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Rigester_new_parent extends AppCompatActivity {

    Button save_change;
    private EditText parent_email;
    ProgressBar progressBar;


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_new_parent);
        mAuth = FirebaseAuth.getInstance();

        progressBar=findViewById(R.id.waiting_bar_for_signup_parent);
        parent_email=findViewById(R.id.email_for_parent_new_registerd);


        save_change=findViewById(R.id.save_parent_information);
        save_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(parent_email.getText().toString(),"123456")
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful())
                                {
                                    FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("idintity")
                                            .setValue("2");
                                    FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("password")
                                            .setValue("123456");
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),"Registered Successfully", Toast.LENGTH_LONG).show();

                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

    }
}
