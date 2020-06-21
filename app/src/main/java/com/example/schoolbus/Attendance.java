package com.example.schoolbus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.schoolbus.Main_Teacher.*;

public class Attendance extends AppCompatActivity {

    public static int is_checked_attend=0;
    final ArrayList<Student> list_of_students = new ArrayList<>();
    TextView date_time;
    Button clear;
    RecyclerView students_recyclerView;
    DatabaseReference reference;
    FirebaseAuth auth;
    AttendanceAdapter attendanceAdapter;
    FirebaseUser firebaseUser;
    static  int count=1;

    public static ArrayList<LatLng>all_locations_after_attendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        FirebaseApp.initializeApp(this);
        auth=FirebaseAuth.getInstance();
        students_recyclerView = findViewById(R.id.list_of_student);
        students_recyclerView.setLayoutManager(new LinearLayoutManager(this));



        reference = FirebaseDatabase.getInstance().getReference().child(auth.getCurrentUser().getUid()).child("BusTrip");
       reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String names="";
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    int ab=0;
                   // String a=dataSnapshot1.child("absent").getValue().toString();
                    if(dataSnapshot1.hasChild("absent"))
                    {
                        ab=Integer.valueOf(dataSnapshot1.child("absent").getValue().toString());
                    }
                    String home_name=dataSnapshot1.child("home_name").getValue().toString();
                    double la=Double.valueOf(dataSnapshot1.child("latLng").child("latitude").getValue().toString());
                    double lo=Double.valueOf(dataSnapshot1.child("latLng").child("longitude").getValue().toString());
                    LatLng latLng=new LatLng(la,lo);
                    Student s = new Student(home_name,dataSnapshot1.getKey(),latLng,ab);
                    list_of_students.add(s);

                }

               attendanceAdapter = new AttendanceAdapter(Attendance.this, list_of_students);
               students_recyclerView.setAdapter(attendanceAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Some thing is wrong", Toast.LENGTH_LONG).show();

            }
        });





        attendanceAdapter = new AttendanceAdapter(Attendance.this, list_of_students);
        students_recyclerView.setAdapter(attendanceAdapter);


        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
       date_time=findViewById(R.id.C_date);
       date_time.setText("Date:"+currentDate);



    }


    public void save_data_absent(View view) {
        String notification_absence;
        all_locations_after_attendance=new ArrayList<>();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        notification_absence = currentDate+"\n";
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
        String datetime = ft.format(dNow);
        ArrayList<Attendance_Information> attendance_informations=new ArrayList<>();
        for(int i=0;i<list_of_students.size();i++)
        {
            if(list_of_students.get(i).isIs_selected()) {

               // Toast.makeText(getApplicationContext(), list_of_students.get(i).getStudentName(), Toast.LENGTH_LONG).show();
                Attendance_Information at=new Attendance_Information(list_of_students.get(i).getStudentName());
                attendance_informations.add(at);
                if(list_of_students.size()>2){
                FirebaseDatabase.getInstance().getReference().child(list_of_students.get(i).getParent_uid()).child("Attendance").child(date_time.getText().toString())
                      .child(attendance_informations.get(i).getStudentName()).setValue(attendance_informations.get(i).getStudentName());
                FirebaseDatabase.getInstance().getReference().child(list_of_students.get(i).getParent_uid()).child("New_Notification")
                        .child("Notify from teacher").setValue(notification_absence+list_of_students.get(i).getStudentName()+" is absencent\n");

                FirebaseDatabase.getInstance().getReference().child(list_of_students.get(i).getParent_uid()).child("notfication_History")
                        .child(datetime).setValue(notification_absence+list_of_students.get(i).getStudentName()+" is absencent\n");
                Toast.makeText(getApplicationContext(),"Attendance saved successfully",Toast.LENGTH_LONG).show();}
                startActivity(new Intent(getApplicationContext(), Main_Teacher.class));
                finish();

                FirebaseDatabase.getInstance().getReference().child(auth.getCurrentUser().getUid()).child("BusTrip")
                        .child(list_of_students.get(i).getParent_uid()).child("absent").setValue("1");



            }else if(!list_of_students.get(i).isIs_selected()){


                FirebaseDatabase.getInstance().getReference().child(auth.getCurrentUser().getUid()).child("BusTrip")
                        .child(list_of_students.get(i).getParent_uid()).child("absent").setValue("0");
                FirebaseDatabase.getInstance().getReference().child(auth.getCurrentUser().getUid()).child("BusTrip")
                        .child(list_of_students.get(i).getParent_uid()).child("visited").setValue("0");
               // all_locations_after_attendance.add(list_of_students.get(i).getLocation());

            }

        }
        startActivity(new Intent(getApplicationContext(), RealTimeMap.class));
        finish();

    }

    public void cancel_attendance(View view) {
        startActivity(new Intent(getApplicationContext(), Main_Teacher.class));
        finish();
    }

}


class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyviewHolder> {

    Context context;
    ArrayList<Student> arrayList;

    public AttendanceAdapter(Context c, ArrayList<Student> s)
    {
        context=c;
        arrayList=s;
    }
    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.student_list_for_attendance,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        holder.student_name.setText(arrayList.get(position).getStudentName());
        holder.checkBox.setChecked(arrayList.get(position).isIs_selected());
        if(arrayList.get(position).getAbsent()==1)
        {
            holder.checkBox.setChecked(true);
        }

        holder.setOnItemClickListner(new MyviewHolder.ItemClickListener(){


            @Override
            public void onItemClick(View v, int pos) {
                CheckBox checkBox=(CheckBox)v;
                if(checkBox.isChecked())
                    arrayList.get(pos).setIs_selected(true);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    static class MyviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView student_name;
        CheckBox checkBox;
        ItemClickListener itemClickListener;

        public MyviewHolder(View itemView) {
            super(itemView);
            student_name=itemView.findViewById(R.id.student_name);
            checkBox=itemView.findViewById(R.id.check_attendance);
            checkBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(view,getLayoutPosition());
        }

        public void setOnItemClickListner(ItemClickListener itemClickListener) {

            this.itemClickListener=itemClickListener;
        }

         interface ItemClickListener {
            void onItemClick(View v,int pos);
        }
    }

}
