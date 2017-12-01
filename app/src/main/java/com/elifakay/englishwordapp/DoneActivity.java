package com.elifakay.englishwordapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoneActivity extends AppCompatActivity {

    private TextView txtTotalScore,txtTotalQuestion;
    private ProgressBar progressBar;
    private Button btnTryAgain;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        //Android
        txtTotalScore=(TextView)findViewById(R.id.txtTotalScore);
        txtTotalQuestion=(TextView)findViewById(R.id.txtTotalQuestion);
        btnTryAgain=(Button)findViewById(R.id.btnTryAgain);
        progressBar=(ProgressBar)findViewById(R.id.doneProgressBar);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUId = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId);


        //Get data from bundle and set to view
        int newScore = 0;
        Bundle extra=getIntent().getExtras();
        if(extra != null) {
            int score = extra.getInt("SCORE");
            int totalQuestion = extra.getInt("TOTAL");
            int correctAnswer = extra.getInt("CORRECT");
            String oldScore=extra.getString("OLDSCORE");

            txtTotalScore.setText(String.format("Score : %d", score));
            txtTotalQuestion.setText(String.format("Passed : %d / %d", correctAnswer, totalQuestion));

            progressBar.setMax(totalQuestion);
            progressBar.setProgress(correctAnswer);

            newScore = score + Integer.valueOf(oldScore);
        }

        if(newScore!=0) {
            //User's new score is being updated
            mUserDatabase.child("score").setValue(String.valueOf(newScore)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Your score is uptaded", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "There was an error saving your score", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else if (newScore==0)
        {
            Intent intent=new Intent(DoneActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DoneActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
