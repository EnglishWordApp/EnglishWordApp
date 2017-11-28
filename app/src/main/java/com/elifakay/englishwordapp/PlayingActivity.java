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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Locale;

public class PlayingActivity extends AppCompatActivity implements View.OnClickListener {

    final static long INTERVAL=1000; //1 sec
    final static long TIMEOUT=11000; //10 sec
    int progressValue=0;

    TextToSpeech textToSpeech;
    CountDownTimer countDownTimer;

    int index=0,score=0,thisQuestion=0,totalQuestion,correctAnswer;
    int result;

    ProgressBar progressBar;
    ImageView imgQuestion;
    Button btnA,btnB,btnC,btnD;
    ImageButton imgBtnTextToSpeech;
    TextView txtScore,txtQuestionNum,txtQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        txtScore=(TextView)findViewById(R.id.txtScore);
        txtQuestionNum=(TextView)findViewById(R.id.txtTotalQuestion);
        txtQuestion=(TextView)findViewById(R.id.txtQuestion);
        imgQuestion=(ImageView)findViewById(R.id.imgQuestion);
        imgBtnTextToSpeech=(ImageButton)findViewById(R.id.imgBtnTextToSpeech);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        btnA=(Button)findViewById(R.id.btnAnswerA);
        btnB=(Button)findViewById(R.id.btnAnswerB);
        btnC=(Button)findViewById(R.id.btnAnswerC);
        btnD=(Button)findViewById(R.id.btnAnswerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);


        textToSpeech=new TextToSpeech(PlayingActivity.this, new TextToSpeech.OnInitListener() {
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
                    String text = txtQuestion.getText().toString();
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
                score+=10;
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
                intent.putExtras(dataSend);
                startActivity(intent);
                finish();
            }
            txtScore.setText(String.format("%d",score));
        }
    }

    private void showQuestion(int index) {

        if(index<totalQuestion)
        {
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d / %d",thisQuestion,totalQuestion));
            progressBar.setProgress(0);
            progressValue=0;

            if(Common.questionList.get(index).getIsImageQuestion().equals("true"))
            {
                Picasso.with(getBaseContext())
                        .load(Common.questionList.get(index).getQuestion())
                        .into(imgQuestion);
                imgQuestion.setVisibility(View.VISIBLE);
                txtQuestion.setVisibility(View.INVISIBLE);
                imgBtnTextToSpeech.setVisibility(View.INVISIBLE);
            }
            else
            {
                txtQuestion.setText(Common.questionList.get(index).getQuestion());

                imgQuestion.setVisibility(View.INVISIBLE);
                txtQuestion.setVisibility(View.VISIBLE);
                imgBtnTextToSpeech.setVisibility(View.VISIBLE);
            }
            btnA.setText(Common.questionList.get(index).getAnswerA());
            btnB.setText(Common.questionList.get(index).getAnswerB());
            btnC.setText(Common.questionList.get(index).getAnswerC());
            btnD.setText(Common.questionList.get(index).getAnswerD());

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
