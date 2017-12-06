package com.elifakay.englishwordapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Button btnSignIn,btnSignUp;
    private EditText edtEmail,edtPassword;
    private TextView txtResetPassword;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

        //ProgressDialog
        mLoginProgress = new ProgressDialog(this);

        //Android
        edtEmail=(EditText)findViewById(R.id.edtEmail);
        edtPassword=(EditText)findViewById(R.id.edtPassword);
        txtResetPassword=(TextView)findViewById(R.id.txtResetPassword);
        btnSignUp=(Button)findViewById(R.id.btnSignUp);
        btnSignIn=(Button)findViewById(R.id.btnSignIn);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    LoginUser(email, password);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please fill in all the information!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(register);

            }
        });

        txtResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changePassword = new Intent(MainActivity.this, ChangePasswordActivity.class);
                startActivity(changePassword);
                finish();
            }
        });

    }

    private void LoginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    mLoginProgress.dismiss();

                    FirebaseUser currenUser=mAuth.getCurrentUser();
                    String currentUId = currenUser.getUid();

                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId);
                    mUserDatabase.keepSynced(true);

                    Intent homeIntent=new Intent(MainActivity.this,HomeActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(homeIntent);
                    finish();

                } else
                {
                    mLoginProgress.hide();
                    Toast.makeText(MainActivity.this,"Cannot Sign in.Please check the from and try again.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if user signed in and update UI accordingly
        FirebaseUser currenUser=mAuth.getCurrentUser();
        if(currenUser != null)
        {
            sendToStart();
        }
    }
    private void sendToStart() {
        Intent startIntent=new Intent(MainActivity.this,HomeActivity.class);
        startActivity(startIntent);
        finish();
    }
}
