package com.elifakay.englishwordapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.elifakay.englishwordapp.Common.Common;
import com.elifakay.englishwordapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    MaterialEditText edtNewUserName, edtNewPassword, edtNewEmail;
    MaterialEditText edtUserName, edtPassword;

    Button btnSignUp, btnSignIn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("Users");

        edtUserName = (MaterialEditText) findViewById(R.id.edtUserName);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);


        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(edtUserName.getText().toString(),edtPassword.getText().toString());
            }
        });
    }

    private void signIn(final String user, final String pwd) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user).exists()) {
                    if (!user.isEmpty()) {

                        User login = dataSnapshot.child(user).getValue(User.class);
                        if (login.getPassword().equals(pwd))
                        {
                            Intent homeActivity=new Intent(MainActivity.this,HomeActivity.class);
                            Common.currentUser=login;
                            startActivity(homeActivity);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Check your password !", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter your username", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "User not available", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showSignUpDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Register");
        alertDialog.setMessage("Please fill in all the information");

        LayoutInflater inflater = this.getLayoutInflater();
        View sing_up_layout = inflater.inflate(R.layout.sign_up_layout, null);

        edtNewUserName = (MaterialEditText) sing_up_layout.findViewById(R.id.edtNewUserName);
        edtNewPassword = (MaterialEditText) sing_up_layout.findViewById(R.id.edtNewPassword);
        edtNewEmail = (MaterialEditText) sing_up_layout.findViewById(R.id.edtNewEmail);

        alertDialog.setView(sing_up_layout);
        alertDialog.setIcon(R.drawable.ic_person_black_24dp);

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                final User user = new User(edtNewUserName.getText().toString(),
                        edtNewPassword.getText().toString(),
                        edtNewEmail.getText().toString());
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(user.getUserName()).exists())
                        {
                            Toast.makeText(MainActivity.this,"User already exists !",Toast.LENGTH_SHORT).show();
                        }else{
                            usersRef.child(user.getUserName()).setValue(user);
                            Toast.makeText(MainActivity.this,"User registration successful",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
}

