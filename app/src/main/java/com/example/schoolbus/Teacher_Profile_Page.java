package com.example.schoolbus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Teacher_Profile_Page extends AppCompatActivity {

    EditText reset_pass,reset_name;
    FirebaseAuth firebaseAuth;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__profile__page);
        reset_pass=findViewById(R.id.teacher_password_changed);
        save=findViewById(R.id.update_teacher_data);
        reset_name=findViewById(R.id.teacher_name_updated);
        firebaseAuth=FirebaseAuth.getInstance();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("name")
                        .setValue(reset_name.getText().toString());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.updatePassword(reset_pass.getText().toString());
                FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("password")
                        .setValue(reset_pass.getText().toString());
            }
        });


    }
}
