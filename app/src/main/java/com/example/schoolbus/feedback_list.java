package com.example.schoolbus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.model.LatLng;

import java.util.ArrayList;

import static com.example.schoolbus.Main_Teacher.Location_area_for_teacher;

public class feedback_list extends AppCompatActivity {

    final ArrayList<feedback_message_class> list_of_feedBack = new ArrayList<>();
    RecyclerView feedback_recyclerView;
    DatabaseReference reference;
    FirebaseAuth auth;
    FeedBackAdabter feedBackAdabter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);
        feedback_recyclerView=findViewById(R.id.list_of_fedback);
        FirebaseApp.initializeApp(this);
        auth=FirebaseAuth.getInstance();
        feedback_recyclerView.setLayoutManager(new LinearLayoutManager(this));


        reference = FirebaseDatabase.getInstance().getReference().child("feed_back");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                  String message = dataSnapshot1.getValue().toString();
                  feedback_message_class fe=new feedback_message_class(message);
                  list_of_feedBack.add(fe);



                }

                feedBackAdabter = new FeedBackAdabter(feedback_list.this, list_of_feedBack);
                feedback_recyclerView.setAdapter(feedBackAdabter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Some thing is wrong", Toast.LENGTH_LONG).show();

            }
        });





        feedBackAdabter = new FeedBackAdabter(feedback_list.this, list_of_feedBack);
        feedback_recyclerView.setAdapter(feedBackAdabter);








    }
}


class FeedBackAdabter extends RecyclerView.Adapter<FeedBackAdabter.MyviewHolder> {

    private Context context;
    private ArrayList<feedback_message_class> arrayList;

    public FeedBackAdabter(Context c, ArrayList<feedback_message_class> s)
    {
        context=c;
        arrayList=s;
    }
    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.feedback_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
     //   holder.date_feedback.setText(arrayList.get(position).getDate());
        holder.message_feedback.setText(arrayList.get(position).getMessage());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class MyviewHolder extends RecyclerView.ViewHolder {
        TextView date_feedback,message_feedback;

        public MyviewHolder(View itemView) {
            super(itemView);
            //date_feedback=itemView.findViewById(R.id.feed_back_date);
            message_feedback=itemView.findViewById(R.id.feed_back_message);
        }

    }
}
