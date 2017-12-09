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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtNewUserName, edtNewPassword, edtNewEmail;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mRegisterProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ProgressDialog
        mRegisterProgress = new ProgressDialog(this);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        //Android
        edtNewUserName=(EditText)findViewById(R.id.edtNewUserName);
        edtNewPassword=(EditText)findViewById(R.id.edtNewPassword);
        edtNewEmail=(EditText)findViewById(R.id.edtNewEmail);
        btnRegister=(Button)findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtNewUserName.getText().toString();
                String email = edtNewEmail.getText().toString();
                String password = edtNewPassword.getText().toString();

                if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password))
                {
                    if(password.length() >= 6)
                    {
                        mRegisterProgress.setTitle("Registering User");
                        mRegisterProgress.setMessage("Please wait while we create your account !");
                        mRegisterProgress.setCanceledOnTouchOutside(false);
                        mRegisterProgress.show();

                        RegisterUser(name, email, password);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"The password must be at least 6 characters",Toast.LENGTH_SHORT).show();
                    }
                } else {
                     Toast.makeText(getApplicationContext(),"Please fill in all the information",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void RegisterUser(final String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uId = currentUser.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

                            HashMap<String,String> userMap=new HashMap<>();
                            userMap.put("name",name);
                            userMap.put("score","0");

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mRegisterProgress.dismiss();

                                        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterActivity.this, "Cannot Save in.Please check the from and try again.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            mRegisterProgress.hide();
                            Toast.makeText(RegisterActivity.this, "Cannot Sign in.Please check the from and try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
