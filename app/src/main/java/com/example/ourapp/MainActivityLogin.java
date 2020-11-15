package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
        if (CommonMethods.isStudent(userID)) {
            goToTeachersList();
        }
        //tutor
        else if(CommonMethods.isTutor(userID)) {
            goToTutorForm();
        }

    }




    public void login (View view) {
        //Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
        /*Intent intent = new Intent(getApplicationContext(), TeachersListActivity.class);
        startActivity(intent);*/

        // hide keyboard if button is pressed
        hideKeyboard();


        String email = CommonMethods.getEmail(username);
        String pass = CommonMethods.getPassword(password);

        if(CommonMethods.checkIfEmpty(email)) {
            //username is the email, didn't rename because it's causing trouble
            //username.setError("Email is required!");
            CommonMethods.warning(username, getString(R.string.emailError));
            return;
        }

        if(CommonMethods.isNotAnEmail(email)) {
            //username.setError("Please enter a valid email!");
            CommonMethods.warning(username, getString(R.string.emailValid));
            return;
        }

        if(CommonMethods.checkIfEmpty(pass)) {
            //password.setError("Password is required!");
            CommonMethods.warning(password, getString(R.string.passwordError));
            return;
        }

        //not necessary
        if(CommonMethods.checkIfPassLengthNotValid(pass)) {
            //password.setError("Minimum password length is 6 characters!");
            CommonMethods.warning(password, getString(R.string.passwordLength));
            return;
        }


        CommonMethods.displayLoadingScreen(progressDialog);
        signIn(email, pass);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return super.onTouchEvent(event);
    }

    public SessionManagement createSession() {
        SessionManagement sessionManagement=new SessionManagement(MainActivityLogin.this);
        return sessionManagement;
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
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("username", getUserName).apply();
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("RATING",0).apply();
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
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("username", getUserName).apply();
                                startActivity(intent);
                                finish();
                                //Toast.makeText(MainActivityLogin.this, "tutor type", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //Toast.makeText(MainActivityLogin.this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
                            CommonMethods.makeToast(MainActivityLogin.this, "Something wrong happened!");
                        }
                    });

                    //}

                   /* else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivityLogin.this, "Check your email to verify your account.", Toast.LENGTH_SHORT).show();
                    }*/

                    //progressBarLogin.setVisibility(View.INVISIBLE);
                    try {
                        if ((progressDialog != null) && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (final Exception e) {
                        // Handle or log or ignore
                        e.printStackTrace();
                    } finally {
                        progressDialog = null;
                    }
                    //progressDialog.dismiss();
                }
                else {
                    //Toast.makeText(MainActivityLogin.this, "Failed to login! Please check your credentials.", Toast.LENGTH_SHORT).show();
                    CommonMethods.makeToast(MainActivityLogin.this, "Failed to login! Please check your credentials.");
                    //progressBarLogin.setVisibility(View.INVISIBLE);
                    try {
                        if ((progressDialog != null) && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (final Exception e) {
                        // Handle or log or ignore
                        e.printStackTrace();
                    } finally {
                        progressDialog = null;
                    }
                    //progressDialog.dismiss();
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