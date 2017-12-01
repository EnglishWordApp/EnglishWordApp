package com.elifakay.englishwordapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elifakay.englishwordapp.Common.Common;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by elf_4 on 26.10.2017.
 */

public class ProfileFragment extends Fragment {

    View myFragment;

    DatabaseReference rankingRef;

    TextView txtProfileUserName,txtProfileScore,txtProfileRanking;

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rankingRef = FirebaseDatabase.getInstance().getReference().child("Ranking").child(Common.currentUser.getUserName());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        myFragment = inflater.inflate(R.layout.fragment_profile, container, false);

        txtProfileUserName = (TextView) myFragment.findViewById(R.id.txtProfileUserName);
        txtProfileScore = (TextView) myFragment.findViewById(R.id.txtProfileScore);
        txtProfileRanking = (TextView) myFragment.findViewById(R.id.txtProfileRanking);

        txtProfileUserName.setText(Common.currentUser.getUserName());

       rankingRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               String score = dataSnapshot.child("score").getValue().toString();
               String ranking = dataSnapshot.child("ranking").getValue().toString();

               txtProfileScore.setText("Score : "+ score);
               txtProfileRanking.setText("Ranking : "+ranking);
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });



        return myFragment;
    }
}