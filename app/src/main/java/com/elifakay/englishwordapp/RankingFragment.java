package com.elifakay.englishwordapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RankingFragment extends Fragment {

    
        });
    }
}