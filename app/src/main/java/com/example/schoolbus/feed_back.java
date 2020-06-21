package com.example.schoolbus;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class feed_back extends AppCompatActivity {
    String answer;
    private Button feedback;
    private RatingBar ratingBar;
    private TextView textView;
    private ImageView imageView;
    private Animation animation;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private EditText feed_back_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        feedback = findViewById(R.id.button);
        feed_back_parent=findViewById(R.id.feed_back_parent);
        ratingBar = findViewById(R.id.ratingBar2);
        textView = findViewById(R.id.textView2);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.feed_back_anim);
        imageView = findViewById(R.id.imageView);
        imageView.startAnimation(animation);

        feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                            answer = String.valueOf((int) ratingBar.getRating());
                            if (answer.equals("1")) {
                                imageView.setImageResource(R.drawable.one_star);
                                textView.setText("could be better");
                                imageView.startAnimation(animation);
                                String date = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault()).format(new Date());
                                databaseReference.child("feed_back").child(String.valueOf(System.currentTimeMillis())).setValue(date + "\n" + "feed back :" + "\n" + textView.getText().toString()+"\n" +feed_back_parent.getText().toString()+ "\n");

                            } else if (answer.equals("2")) {
                                imageView.setImageResource(R.drawable.two_star);
                                textView.setText("good enough");
                                imageView.startAnimation(animation);
                                String date = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault()).format(new Date());
                                databaseReference.child("feed_back").child(String.valueOf(System.currentTimeMillis())).setValue(date + "\n" + "feed back :" + "\n" + textView.getText().toString()+"\n" +feed_back_parent.getText().toString()+ "\n");
                            } else if (answer.equals("3")) {
                                imageView.setImageResource(R.drawable.three_star);
                                textView.setText("not bad");
                                imageView.startAnimation(animation);
                                String date = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault()).format(new Date());
                                databaseReference.child("feed_back").child(String.valueOf(System.currentTimeMillis())).setValue(date + "\n" + "feed back :" + "\n" + textView.getText().toString()+"\n"+feed_back_parent.getText().toString() + "\n");
                            } else if (answer.equals("4")) {
                                imageView.setImageResource(R.drawable.four_stars);
                                textView.setText("very good");
                                imageView.startAnimation(animation);
                                String date = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault()).format(new Date());
                                databaseReference.child("feed_back").child(String.valueOf(System.currentTimeMillis())).setValue(date + "\n" + "feed back :" + "\n" + textView.getText().toString()+"\n" +feed_back_parent.getText().toString()+ "\n");
                            } else if (answer.equals("5")) {
                                imageView.setImageResource(R.drawable.five_stars);
                                textView.setText("the best service");
                                imageView.startAnimation(animation);
                                String date = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault()).format(new Date());
                                databaseReference.child("feed_back").child(String.valueOf(System.currentTimeMillis())).setValue(date + "\n" + "feed back :" + "\n" + textView.getText().toString()+"\n" +feed_back_parent.getText().toString()+ "\n");
                            }
                            Toast.makeText(getApplicationContext(), "thanks for your feedback", Toast.LENGTH_LONG).show();


                        }
                    });

                }
        });
    }
}
