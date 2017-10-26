package com.elifakay.englishwordapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.elifakay.englishwordapp.Common.Common;
import com.elifakay.englishwordapp.Model.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

/**
 * Created by elf_4 on 26.10.2017.
 */

public class StartFragment extends Fragment {

    View myFragment;
    Button btnPlay;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseRefQuestions;

    public static StartFragment newInstance() {
        StartFragment startFragment = new StartFragment();
        return startFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRefQuestions = firebaseDatabase.getReference("Questions");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_start, container, false);

        loadQuestions();

        btnPlay = (Button) myFragment.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlayingActivity.class);
                startActivity(intent);
            }
        });

        return myFragment;
    }

    private void loadQuestions() {

        if (Common.questionList.size() > 0)
            Common.questionList.clear();

        databaseRefQuestions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Question ques = postSnapshot.getValue(Question.class);
                    Common.questionList.add(ques);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Random List
        Collections.shuffle(Common.questionList);
    }
}

