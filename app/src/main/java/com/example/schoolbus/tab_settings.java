package com.example.schoolbus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class tab_settings extends Fragment {

    private final List<String> not = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ListView listView;
    private OnFragmentInteractionListener mListener;
    private FloatingActionButton feed_back;
    /////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////

    public tab_settings() {

    }


    public static tab_settings newInstance(String param1, String param2) {
        tab_settings fragment = new tab_settings();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_tab_settings, container, false);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("notfication_History");
        feed_back = view.findViewById(R.id.feed_back);
        feed_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),feed_back.class));
            }
        });
        listView = view.findViewById(R.id.list_not);
        listView.setFooterDividersEnabled(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                not.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    not.add(ds.getValue(String.class));

                }
                ArrayAdapter<String> arrayAdapter;
                if(getContext()!=null){
                if(not.size()>0) {
                    arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()).getApplicationContext(), android.R.layout.simple_list_item_activated_1, not);
                listView.setAdapter(arrayAdapter);}
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
