package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivityLogin extends AppCompatActivity {

    private FirebaseAuth mAuth;

    DatabaseReference databaseReference;

    ImageView imageView;
    EditText username;//email
    EditText password;
    Button loginButton;
    TextView signupLink;
    TextView resetPassword;

    ProgressBar progressBarLogin;

    public void login (View view) {
        //Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
        /*Intent intent = new Intent(getApplicationContext(), TeachersListActivity.class);
        startActivity(intent);*/

        String email = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(email.isEmpty()){
            //username is the email, didn't rename because it's causing trouble
            username.setError("Email is required!");
            username.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            username.setError("Please enter a valid email!");
            username.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }

        //not necessary
        if(pass.length() < 6){
            password.setError("Minimum password length is 6 characters!");
            password.requestFocus();
            return;
        }

        progressBarLogin.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(MainActivityLogin.this, "login good", Toast.LENGTH_SHORT).show();

                    //returns false
                    /*boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                    Log.d("isnewUser5? ", String.valueOf(isNew));*/

                    //check is user is a tutor or student, and redirect to corresponding screen
                    String currentuserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserId);

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userType = snapshot.child("userType").getValue().toString();
                            if(userType.equalsIgnoreCase("student")){
                                Intent intent = new Intent(getApplicationContext(), TeachersListActivity.class);
                                startActivity(intent);
                                finish();
                            } else if(userType.equalsIgnoreCase("tutor")){
                                //here we should check if tutor has filled the info form previously,
                                //then redirect her to TeacherEditInfoFrom
                                //else, open the main form that is TeacherFormActivity
                                //this check can be done by searching if there is any data
                                //related to the current tutor id, if not, we should open main form
                                //else, open the editing form..

                                //I will currently redirect tutor to main form until we make
                                //a new class to get tutor info and save it to db
                                Intent intent = new Intent(getApplicationContext(), TeacherFormActivity.class);
                                startActivity(intent);
                                finish();
                                //Toast.makeText(MainActivityLogin.this, "tutor type", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivityLogin.this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    progressBarLogin.setVisibility(View.INVISIBLE);
                }
                else{
                    Toast.makeText(MainActivityLogin.this, "Failed to login! Please check your credentials.", Toast.LENGTH_SHORT).show();
                    progressBarLogin.setVisibility(View.INVISIBLE);
                }
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("TutorApp - Login");

        imageView = (ImageView) findViewById(R.id.logo);
        username = (EditText) findViewById(R.id.usernameEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        signupLink = (TextView) findViewById(R.id.signupLink);
        resetPassword = (TextView) findViewById(R.id.resetPasswordTV);

        progressBarLogin = (ProgressBar) findViewById(R.id.progressBarLogin);

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivitySignUp.class);
                //intent.putExtra("username","mahmoud");

                startActivity(intent);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

    }
}