package com.elifakay.englishwordapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.elifakay.englishwordapp.Common.Common;
import com.elifakay.englishwordapp.Interface.ItemClickListener;
import com.elifakay.englishwordapp.Interface.RankingCallBack;
import com.elifakay.englishwordapp.Model.QuestionScore;
import com.elifakay.englishwordapp.Model.Ranking;
import com.elifakay.englishwordapp.ViewHolder.RankingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RankingFragment extends Fragment {

    View myFragment;

    RecyclerView rankingList;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<Ranking, RankingViewHolder> adapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference updateRankingRef;
    DatabaseReference questionScoreRef, rankingRef;

    int sum = 0;

    private ProgressDialog mProgress;

    public static RankingFragment newInstance() {
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        questionScoreRef = firebaseDatabase.getReference("QuestionScore");
        rankingRef = firebaseDatabase.getReference("Ranking");
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

        updateScore(Common.currentUser.getUserName(), new RankingCallBack<Ranking>() {
            @Override
            public void callBack(Ranking ranking) {
                rankingRef.child(ranking.getUserName()).setValue(ranking);
                // showRanking();
            }
        });

        adapter = new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(
                Ranking.class,
                R.layout.ranking_single_layout,
                RankingViewHolder.class,
                rankingRef.orderByChild("score")
        ) {
            @Override
            protected void populateViewHolder(RankingViewHolder viewHolder, Ranking model, int position) {

                viewHolder.txtRSingleUserName.setText(model.getUserName());
                viewHolder.txtRSingleScore.setText(String.valueOf(model.getScore()));
                viewHolder.txtSingleUserRanking.setText(String.valueOf(model.getRanking()));

                long rankingNumber=Long.valueOf(adapter.getItemCount()-position);
                updateRanking(model.getUserName(),rankingNumber);

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

    private void updateScore(final String userName, final RankingCallBack<Ranking> callBack) {

        questionScoreRef.orderByChild("user").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    QuestionScore questionScore = data.getValue(QuestionScore.class);
                    sum += Integer.parseInt(questionScore.getScore());
                }
                Ranking ranking = new Ranking(userName, sum);
                callBack.callBack(ranking);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateRanking(String userName,long userRanking) {

        updateRankingRef = FirebaseDatabase.getInstance().getReference().child("Ranking").child(userName);
        updateRankingRef.child("ranking").setValue(userRanking).addOnCompleteListener(new OnCompleteListener<Void>() {
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