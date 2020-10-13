package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivitySignUp extends AppCompatActivity {


    private FirebaseAuth mAuth;

    ImageView imageView;
    EditText username;
    EditText email;
    EditText password;
    RadioButton radioButtonTutor;
    RadioButton radioButtonStudent;
    RadioGroup radioGroup;
    Button signup;
    TextView loginLink;


    public void signupAsTeacher(View view){
        Intent intent = new Intent(getApplicationContext(), TeacherFormActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sign_up);

        setTitle("TutorApp - Sign up");

        imageView = (ImageView) findViewById(R.id.logo);
        username = (EditText) findViewById(R.id.usernameETSU);
        email = (EditText) findViewById(R.id.emailET);
        password = (EditText) findViewById(R.id.passwordEditTextSU);
        radioButtonTutor = (RadioButton) findViewById(R.id.radioButtonTeacher);
        radioButtonStudent = (RadioButton) findViewById(R.id.radioButtonStudent);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        signup = (Button) findViewById(R.id.signUpButton);
        loginLink = (TextView) findViewById(R.id.loginLink);


        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivityLogin.class);
                //intent.putExtra("username","mahmoud");
                startActivity(intent);
            }
        });

/*
        if(radioGroup.getCheckedRadioButtonId() == -1){
            Log.d("radio"," No radio buttons are checked");
        }
        else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonStudent){
            Log.d("radio"," Student radio chosen");
        }
        else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonTeacher){
            Log.d("radio"," Tutor radio chosen");
        }
        else{

        }
*/
        mAuth = FirebaseAuth.getInstance();

    }
}