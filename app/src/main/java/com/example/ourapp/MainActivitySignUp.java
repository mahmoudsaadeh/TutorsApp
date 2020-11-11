package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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

    public static final int PERSON_TYPE_NOT_SELECTED = -1;

    public void signUp(View view) {
        String mail = email.getText().toString().trim();
        String name = username.getText().toString().trim();
        String passwordd = password.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();
        int personType = radioGroup.getCheckedRadioButtonId();


        if(name.isEmpty()) {
            CommonMethods.warning(username,getString(R.string.usernameError));
            return;
        }

        if(CommonMethods.checkIfEmpty(mail)) {
            CommonMethods.warning(email,getString(R.string.emailError));
            return;
        }

        if(CommonMethods.isNotAnEmail(mail)) {
            CommonMethods.warning(email,getString(R.string.emailValid));
            return;
        }

        if(CommonMethods.checkIfEmpty(passwordd)) {
            CommonMethods.warning(password,getString(R.string.passwordError));
            return;
        }

        if(CommonMethods.checkIfPassLengthNotValid(passwordd)) {
            CommonMethods.warning(password,getString(R.string.passwordLength));
            return;
        }

        if(CommonMethods.checkIfEmpty(confirmPass)) {
            CommonMethods.warning(confirmPassword,getString(R.string.passwordConfirm));
            return;
        }
        else if(!CommonMethods.checkIfConfirmPassMatchesPass(confirmPass, passwordd)) {
            CommonMethods.warning(confirmPassword,getString(R.string.passwordMatch));
            return;
        }

        if(personType == PERSON_TYPE_NOT_SELECTED) {
            CommonMethods.makeToast(MainActivitySignUp.this, "You should choose a 'user type' before you continue!");
            return;
        }
        else {
            setPersonType(personType);
        }

        CommonMethods.displayLoadingScreen(progressDialog);

        signUp(mail,passwordd,name);

    } // end signUp method


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sign_up);

        getViewsById();
        username.requestFocus();

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressDialog = new ProgressDialog(MainActivitySignUp.this);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });

        /* if(radioGroup.getCheckedRadioButtonId() == -1){
            Log.d("radio"," No radio buttons are checked");
        }
        else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonStudent){
            Log.d("radio"," Student radio chosen");
        }
        else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonTeacher){
            Log.d("radio"," Tutor radio chosen");
        }
        else{

        }*/

        mAuth = FirebaseAuth.getInstance();

    }//end onCreate




    public void setPersonType(int personType) {
        if(personType == R.id.radioButtonStudent) {
            personTypeString = radioButtonStudent.getText().toString();
            //Log.d("radiobtnn:", radioButtonStudent.getText().toString());
        }
        else if(personType == R.id.radioButtonTeacher) {
            personTypeString = radioButtonTeacher.getText().toString();
            //Log.d("radiobtnn:", radioButtonTeacher.getText().toString());
        }
    }


    public void signUp(final String mail, String passwordd,final String name) {
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
                                    if(task.isSuccessful()) {
                                        //Toast.makeText(MainActivitySignUp.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                                        CommonMethods.makeToast(MainActivitySignUp.this, "Sign Up Successful!");
                                        //progressBar.setVisibility(View.GONE);
                                        progressDialog.dismiss();

                                        //redirect to login screen
                                        Intent intent = new Intent(getApplicationContext(), MainActivityLogin.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        //Toast.makeText(MainActivitySignUp.this, "Failed to sign up, please try again.", Toast.LENGTH_SHORT).show();
                                        CommonMethods.makeToast(MainActivitySignUp.this, "Failed to sign up, please try again.");
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
                            //Toast.makeText(MainActivitySignUp.this, "Account already exists! Please try again with a different email address.", Toast.LENGTH_SHORT).show();
                            CommonMethods.makeToast(MainActivitySignUp.this, "Account already exists! Please try again with a different email address.");
                            // progressBar.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    }
                });
    }// end signUp



    public void getViewsById() {
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
    }

    public void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), MainActivityLogin.class);
        //intent.putExtra("username","mahmoud");
        startActivity(intent);
    }


} // end MainActivitySignUp
