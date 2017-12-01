package com.elifakay.englishwordapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

/*
 * Created by elf_4 on 26.10.2017.
 */

public class ProfileFragment extends Fragment {

    private View myFragment;

    private TextView txtProfileUserName, txtProfileScore, txtProfileRanking;
    private Button btnProfileLogOut;

    private DatabaseReference mUserDatabase, mRankingDatabase;
    private FirebaseUser mCurrentUser;

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_profile, container, false);

        //Android
        txtProfileUserName = (TextView) myFragment.findViewById(R.id.txtProfileUserName);
        txtProfileScore = (TextView) myFragment.findViewById(R.id.txtProfileScore);
        txtProfileRanking = (TextView) myFragment.findViewById(R.id.txtProfileRanking);
        btnProfileLogOut = (Button) myFragment.findViewById(R.id.btnProfileLogOut);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUId = mCurrentUser.getUid();

        mRankingDatabase = FirebaseDatabase.getInstance().getReference().child("Ranking").child(currentUId);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String score = dataSnapshot.child("score").getValue().toString();

                txtProfileUserName.setText(name);
                txtProfileScore.setText(score);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        txtProfileRanking.setText("0");

        mRankingDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("rank").getValue() != null) {
                    String rank = dataSnapshot.child("rank").getValue().toString();
                    txtProfileRanking.setText(rank);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnProfileLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent startIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(startIntent);
                getActivity().finish();
            }
        });

        return myFragment;
    }
}