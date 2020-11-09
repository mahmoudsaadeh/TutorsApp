package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivityLogin<checkBox> extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ImageView imageView;
    EditText username;//email
    EditText password;
    Button loginButton;
    TextView signupLink;
    TextView resetPassword;

    ProgressDialog progressDialog;
    //ProgressBar progressBarLogin;

    String getUserName;

    public static String un;


    @Override
    protected void onStart(){
        super.onStart();

        int userID = createSession().getSession();

        //student
        if (isStudent(userID)) {
            goToTeachersList();
        }
        //tutor
        else if(isTutor(userID)) {
            goToTutorForm();
        }

    }




    public void login (View view) {
        //Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
        /*Intent intent = new Intent(getApplicationContext(), TeachersListActivity.class);
        startActivity(intent);*/

        // hide keyboard if button is pressed
        hideKeyboard();


        String email = getEmail();
        String pass =getPassword();

        if(email.isEmpty()) {
            //username is the email, didn't rename because it's causing trouble
            //username.setError("Email is required!");
            requireEmail();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //username.setError("Please enter a valid email!");
            warnForEmail();
            return;
        }

        if(pass.isEmpty()) {
            //password.setError("Password is required!");
            requirePassword();
            return;
        }

        //not necessary
        if(pass.length() < 6) {
            //password.setError("Minimum password length is 6 characters!");
            warnForPassword();
            return;
        }


        displayLoadingScreen();
        signIn(email,pass);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(MainActivityLogin.this);

        try {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        //setTitle("TutorApp - Login");
        findViewsById();

        //progressBarLogin = (ProgressBar) findViewById(R.id.progressBarLogin);


        username.requestFocus();

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSingUp();
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReset();
            }
        });

        password.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();

    }


    public SessionManagement createSession() {
        SessionManagement sessionManagement=new SessionManagement(MainActivityLogin.this);
        return sessionManagement;
    }


    public String getEmail() {
        return username.getText().toString().trim();
    }


    public String getPassword() {
       return password.getText().toString().trim();
    }


    public boolean isStudent(int id) {
        if(id == 1) {
            return true;
        }
        else {
            return false;
        }
    }


    public boolean isTutor(int id) {
        if(id==2) {
            return true;
        }
        else {
            return false;
        }
    }


    public void goToTeachersList() {
        Intent intent = new Intent(getApplicationContext(), TeachersListActivity.class);
        startActivity(intent);
        finish();
    }


    public void goToTutorForm() {
        Intent intent = new Intent(getApplicationContext(), TeacherFormActivity.class);
        startActivity(intent);
        finish();
    }


    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void requireEmail(){
        username.setError(getString(R.string.emailError));
        username.requestFocus();
    }


    public void warnForEmail() {
        username.setError(getString(R.string.emailCheck));
        username.requestFocus();
    }


    public void requirePassword() {
        password.setError(getString(R.string.passwordError));
        password.requestFocus();
    }


    public void warnForPassword() {
        password.setError(getString(R.string.passwordLength));
        password.requestFocus();
    }


    public void displayLoadingScreen() {
        // progressBarLogin.setVisibility(View.VISIBLE);
        progressDialog.show();

        //progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    public void signIn(String email,String pass){
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //Toast.makeText(MainActivityLogin.this, "login good", Toast.LENGTH_SHORT).show();

                    //returns false
                    /*boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                    Log.d("isnewUser5? ", String.valueOf(isNew));*/

                    //email verification
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    //if(user.isEmailVerified()) {
                    //redirect user to profile
                    //check is user is a tutor or student, and redirect to corresponding screen
                    String currentuserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserId);

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String userType = snapshot.child("userType").getValue().toString();
                            getUserName = snapshot.child("username").getValue().toString();


                            if (userType.equalsIgnoreCase("student")) {

                                int id = 1;

                                SessionManagement sessionManagement=new SessionManagement(MainActivityLogin.this);
                                sessionManagement.saveSession(id);

                                Intent intent = new Intent(getApplicationContext(), TeachersListActivity.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    /*intent.putExtra("username", getUserName + "");
                                    Log.i("username",getUserName+"");*/
                                un = getUserName;

                                startActivity(intent);
                                finish();
                            }
                            else if (userType.equalsIgnoreCase("tutor")) {
                                //here we should check if tutor has filled the info form previously,
                                //then redirect her to TeacherEditInfoFrom
                                //else, open the main form that is TeacherFormActivity
                                //this check can be done by searching if there is any data
                                //related to the current tutor id, if not, we should open main form
                                //else, open the editing form..

                                //I will currently redirect tutor to main form until we make
                                //a new class to get tutor info and save it to db


                                int id=2;

                                SessionManagement sessionManagement=new SessionManagement(MainActivityLogin.this);
                                sessionManagement.saveSession(id);


                                Intent intent = new Intent(getApplicationContext(), TeacherFormActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                //intent.putExtra("username", getUserName + "");
                                un = getUserName;

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

                    //}

                   /* else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivityLogin.this, "Check your email to verify your account.", Toast.LENGTH_SHORT).show();
                    }*/

                    //progressBarLogin.setVisibility(View.INVISIBLE);
                    progressDialog.dismiss();
                }
                else {
                    Toast.makeText(MainActivityLogin.this, "Failed to login! Please check your credentials.", Toast.LENGTH_SHORT).show();
                    //progressBarLogin.setVisibility(View.INVISIBLE);
                    progressDialog.dismiss();
                }
            }
        });
    }


    public void findViewsById() {
        imageView = (ImageView) findViewById(R.id.logo);
        username = (EditText) findViewById(R.id.usernameEditText);//email
        password = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        signupLink = (TextView) findViewById(R.id.signupLink);
        resetPassword = (TextView) findViewById(R.id.resetPasswordTV);
    }


    public void goToSingUp(){
        Intent intent = new Intent(getApplicationContext(), MainActivitySignUp.class);
        //intent.putExtra("username","mahmoud");
        startActivity(intent);
    }


    public void goToReset(){
        Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
        startActivity(intent);
    }


}//end MainActivityLogin