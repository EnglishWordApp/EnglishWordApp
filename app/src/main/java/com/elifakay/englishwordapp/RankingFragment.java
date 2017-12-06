package com.elifakay.englishwordapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.elifakay.englishwordapp.Common.Common;
import com.elifakay.englishwordapp.Interface.ItemClickListener;
import com.elifakay.englishwordapp.Interface.RankCallBack;
import com.elifakay.englishwordapp.Model.Rank;
import com.elifakay.englishwordapp.Model.Users;
import com.elifakay.englishwordapp.ViewHolder.RankViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class RankingFragment extends Fragment {

    private View myFragment;

    private RecyclerView rankingList;
    private LinearLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Rank, RankViewHolder> adapter;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mRankingDatabase,mUpdateRankingDatabase,mUserDatabase;

    private ProgressDialog mProgress;

    public static RankingFragment newInstance() {
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_ranking, container, false);

        rankingList = (RecyclerView) myFragment.findViewById(R.id.rankingList);
        layoutManager = new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);

        //Because orderByChild method of Firebase will sort list with asc
        //So we need reverse our Recycle data by layoutManager
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);

        //ProgressDialog
        mProgress = new ProgressDialog(getActivity());
        mProgress.setTitle("Ranking");
        mProgress.setMessage("Please wait while ranking user");
        mProgress.show();

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUId = mCurrentUser.getUid();

        mRankingDatabase = FirebaseDatabase.getInstance().getReference().child("Ranking");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId);

        saveUserRank(currentUId, new RankCallBack<Rank>() {
            @Override
            public void callBack(Rank rank) {
                mRankingDatabase.child(currentUId).setValue(rank);
                // showRanking();
            }
        });

        adapter = new FirebaseRecyclerAdapter<Rank, RankViewHolder>(
                Rank.class,
                R.layout.rank_single_layout,
                RankViewHolder.class,
                mRankingDatabase.orderByChild("score")
        ) {
            @Override
            protected void populateViewHolder(RankViewHolder viewHolder, Rank model, int position) {

                viewHolder.txtRankSingleUserName.setText(model.getName());
                viewHolder.txtRankSingleScore.setText(String.valueOf(model.getScore()));
                //viewHolder.txtRankSingle.setText(String.valueOf(model.getRank()));

                String b=model.getName();
                int a=adapter.getItemCount();

                long rankingNumber = Long.valueOf(adapter.getItemCount() - position);
                updateRanking(currentUId, rankingNumber);

                viewHolder.setItemItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);

        return myFragment;
    }

    private void saveUserRank(String currentUId, final RankCallBack<Rank> rankCallBack) {

        //User information
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String score=dataSnapshot.child("score").getValue().toString();

                Rank ranking = new Rank(name,Long.parseLong(score));
                rankCallBack.callBack(ranking);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateRanking(String currentUId, long userRank) {

        mUpdateRankingDatabase = FirebaseDatabase.getInstance().getReference().child("Ranking").child(currentUId);
        mUpdateRankingDatabase.child("rank").setValue(userRank).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mProgress.dismiss();
                } else {
                    Toast.makeText(getActivity(), "There was some error in saving Changes", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}