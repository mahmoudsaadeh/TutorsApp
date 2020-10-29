package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivitySignUp extends AppCompatActivity {


    private FirebaseAuth mAuth;

    ImageView imageView;
    EditText username;
    EditText email;
    EditText password;
    EditText confirmPassword;
    RadioButton radioButtonTeacher;
    RadioButton radioButtonStudent;
    RadioGroup radioGroup;
    Button signup;
    TextView loginLink;

    //ProgressBar progressBar;
    ProgressDialog progressDialog;

    String personTypeString = "";

    public void signUp(View view){
        /*Intent intent = new Intent(getApplicationContext(), TeacherFormActivity.class);
        startActivity(intent);*/
        final String mail = email.getText().toString().trim();
        final String name = username.getText().toString().trim();
        String passwordd = password.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();
        int personType = radioGroup.getCheckedRadioButtonId();


        if(name.isEmpty()){
            username.setError("Username is required!");
            username.requestFocus();
            return;
        }

        if(mail.isEmpty()){
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            email.setError("Please provide a correct email address!");
            email.requestFocus();
            return;
        }

        if(passwordd.isEmpty()){
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }

        if(passwordd.length() < 6){
            password.setError("Minimum password length is 6 characters!");
            password.requestFocus();
            return;
        }

        if(confirmPass.isEmpty()){
            confirmPassword.setError("You need to confirm your password!");
            confirmPassword.requestFocus();
            return;
        } else if(!confirmPass.equals(passwordd)){
            confirmPassword.setError("Your passwords doesn't match! Please recheck.");
            confirmPassword.requestFocus();
            return;
        }

        if(personType == -1){
            Toast.makeText(this, "You should choose a 'user type' before you continue!", Toast.LENGTH_SHORT).show();
            return;
        }else {
            if(personType == R.id.radioButtonStudent){
                personTypeString = radioButtonStudent.getText().toString();
                //Log.d("radiobtnn:", radioButtonStudent.getText().toString());
            }
            else if(personType == R.id.radioButtonTeacher){
                personTypeString = radioButtonTeacher.getText().toString();
                //Log.d("radiobtnn:", radioButtonTeacher.getText().toString());
            }
        }

        //progressBar.setVisibility(View.VISIBLE);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        mAuth.createUserWithEmailAndPassword(mail, passwordd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final UserClass newUser = new UserClass(name, mail, personTypeString);

                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivitySignUp.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                                        //progressBar.setVisibility(View.GONE);
                                        progressDialog.dismiss();

                                        //redirect to login screen
                                        Intent intent = new Intent(getApplicationContext(), MainActivityLogin.class);
                                        startActivity(intent);
                                        finish();

                                        /*if(personTypeString.equalsIgnoreCase("tutor")){
                                            Intent intent = new Intent(getApplicationContext(), TeacherFormActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            //redirect to login screen
                                            Intent intent = new Intent(getApplicationContext(), MainActivityLogin.class);
                                            startActivity(intent);
                                        }*/
                                    }
                                    else {
                                        Toast.makeText(MainActivitySignUp.this, "Failed to sign up, please try again.", Toast.LENGTH_SHORT).show();
                                        //progressBar.setVisibility(View.GONE);
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                            /*
                            //returns true
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d("isnewUser5? ", String.valueOf(isNew));*/

                        }
                        else {
                            Toast.makeText(MainActivitySignUp.this, "Account already exists! Please try again with a different email address.", Toast.LENGTH_SHORT).show();
                           // progressBar.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    }
                });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sign_up);

        setTitle("TutorApp - Sign up(branch-hadi)");

        imageView = (ImageView) findViewById(R.id.logo);
        username = (EditText) findViewById(R.id.usernameETSU);
        email = (EditText) findViewById(R.id.emailET);
        password = (EditText) findViewById(R.id.passwordEditTextSU);
        confirmPassword = (EditText) findViewById(R.id.confirmPasswordET);
        radioButtonTeacher = (RadioButton) findViewById(R.id.radioButtonTeacher);
        radioButtonStudent = (RadioButton) findViewById(R.id.radioButtonStudent);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        signup = (Button) findViewById(R.id.signUpButton);
        loginLink = (TextView) findViewById(R.id.loginLink);

        username.requestFocus();


        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressDialog = new ProgressDialog(MainActivitySignUp.this);

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