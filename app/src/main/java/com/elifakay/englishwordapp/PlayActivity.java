package com.elifakay.englishwordapp;

import android.content.Intent;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elifakay.englishwordapp.Common.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    final static long INTERVAL=1000; //1 sec
    final static long TIMEOUT=11000; //10 sec
    int progressValue=0;

    private TextToSpeech textToSpeech;
    private CountDownTimer countDownTimer;

    int index=0,score=0,thisQuestion=0,totalQuestion,correctAnswer;
    int result;
    String oldScore="0";

    private ProgressBar progressBar;

    private ImageView imgPlayQuestion;
    private ImageButton imgBtnTextToSpeech;
    private TextView txtPlayQuestion,txtPlayScore,txtPlayTotalQuestion;
    private Button btnAnswerA,btnAnswerB,btnAnswerC,btnAnswerD;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUId = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                oldScore=dataSnapshot.child("score").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Android
        imgPlayQuestion=(ImageView)findViewById(R.id.imgPlayQuestion);
        imgBtnTextToSpeech=(ImageButton)findViewById(R.id.imgBtnTextToSpeech);
        txtPlayQuestion=(TextView)findViewById(R.id.txtPlayQuestion);
        txtPlayScore=(TextView)findViewById(R.id.txtPlayScore);
        txtPlayTotalQuestion=(TextView)findViewById(R.id.txtPlayTotalQuestion);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        btnAnswerA=(Button)findViewById(R.id.btnAnswerA);
        btnAnswerB=(Button)findViewById(R.id.btnAnswerB);
        btnAnswerC=(Button)findViewById(R.id.btnAnswerC);
        btnAnswerD=(Button)findViewById(R.id.btnAnswerD);


        btnAnswerA.setOnClickListener(this);
        btnAnswerB.setOnClickListener(this);
        btnAnswerC.setOnClickListener(this);
        btnAnswerD.setOnClickListener(this);

        textToSpeech=new TextToSpeech(PlayActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status== TextToSpeech.SUCCESS)
                {
                    result=textToSpeech.setLanguage(Locale.UK);
                }else{
                    Toast.makeText(getApplicationContext(),"Feature not supported in your device",Toast.LENGTH_LONG).show();
                }
            }
        });

        imgBtnTextToSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTS(v);
            }
        });
    }
    public void TTS(View view) {
        switch (view.getId()) {
            case R.id.imgBtnTextToSpeech:
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_LONG).show();
                } else {
                    String text = txtPlayQuestion.getText().toString();
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
                // textToSpeech.stop();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(textToSpeech!=null)
        {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onClick(View view) {
        countDownTimer.cancel();
        if(index<totalQuestion)
        {
            Button btnClicked=(Button)view;
            if(btnClicked.getText().equals(Common.questionList.get(index).getCorrectAnswer()))
            {
                //Choose correct answer
                score+=2;
                correctAnswer++;
                showQuestion(++index);
            }else
            {
                //Choose wrong answer
                Intent intent=new Intent(this,DoneActivity.class);
                Bundle dataSend=new Bundle();
                dataSend.putInt("SCORE",score);
                dataSend.putInt("TOTAL",totalQuestion);
                dataSend.putInt("CORRECT",correctAnswer);
                dataSend.putString("OLDSCORE",oldScore);
                intent.putExtras(dataSend);
                startActivity(intent);
                finish();
            }
            txtPlayScore.setText(String.format("%d",score));
        }
    }

    private void showQuestion(int index) {

        if(index<totalQuestion)
        {
            thisQuestion++;
            txtPlayTotalQuestion.setText(String.format("%d / %d",thisQuestion,totalQuestion));
            progressBar.setProgress(0);
            progressValue=0;

            if(Common.questionList.get(index).getIsImageQuestion().equals("true"))
            {
                Picasso.with(getBaseContext())
                        .load(Common.questionList.get(index).getQuestion())
                        .into(imgPlayQuestion);
                imgPlayQuestion.setVisibility(View.VISIBLE);
                txtPlayQuestion.setVisibility(View.INVISIBLE);
                imgBtnTextToSpeech.setVisibility(View.INVISIBLE);
            }
            else
            {
                txtPlayQuestion.setText(Common.questionList.get(index).getQuestion());

                imgPlayQuestion.setVisibility(View.INVISIBLE);
                txtPlayQuestion.setVisibility(View.VISIBLE);
                imgBtnTextToSpeech.setVisibility(View.VISIBLE);
            }
            btnAnswerA.setText(Common.questionList.get(index).getAnswerA());
            btnAnswerB.setText(Common.questionList.get(index).getAnswerB());
            btnAnswerC.setText(Common.questionList.get(index).getAnswerC());
            btnAnswerD.setText(Common.questionList.get(index).getAnswerD());

            countDownTimer.start(); //Start Time
        }
        else
        {
            //If it is final question
            Intent intent=new Intent(this,DoneActivity.class);
            Bundle dataSend=new Bundle();
            dataSend.putInt("SCORE",score);
            dataSend.putInt("TOTAL",totalQuestion);
            dataSend.putInt("CORRECT",correctAnswer);
            dataSend.putString("OLDSCORE",oldScore);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        totalQuestion= Common.questionList.size();

        countDownTimer=new CountDownTimer(TIMEOUT,INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }
}
