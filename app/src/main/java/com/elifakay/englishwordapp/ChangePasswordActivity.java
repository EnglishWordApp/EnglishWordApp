package com.elifakay.englishwordapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText edtChangeEmail;
    Button btnChangePassword;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth=FirebaseAuth.getInstance();
        mUser=FirebaseAuth.getInstance().getCurrentUser();

        edtChangeEmail=(EditText)findViewById(R.id.edtChangeEmail);
        btnChangePassword=(Button)findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(edtChangeEmail.getText()))
                {
                    mAuth.sendPasswordResetEmail(edtChangeEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "The password reset link has been sent to your email", Toast.LENGTH_LONG).show();
                                        edtChangeEmail.setText("");
                                        Intent mainIntent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });
                }else
                {
                    Toast.makeText(getApplicationContext(), "Please enter your email address!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
