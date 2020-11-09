package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText resetPasswordEmailEt;
    private Button resetPasswordButton;
    private ProgressBar progressBarResetPassword;

    FirebaseAuth firebaseAuth;

    public void resetPassword (View view){
        String email = resetPasswordEmailEt.getText().toString().trim();

        if(email.isEmpty()){
            //username is the email, didn't rename because it's causing trouble
            //resetPasswordEmailEt.setError("Email is required!");
            requireEmail();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //resetPasswordEmailEt.setError("Please enter a valid email!");
            emailFormatError();
            return;
        }

        progressBarResetPassword.setVisibility(View.VISIBLE);

        resetPassword(email);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetPasswordEmailEt = (EditText) findViewById(R.id.resetPassEmailET);
        resetPasswordButton = (Button) findViewById(R.id.resetPasswordButton);
        progressBarResetPassword = (ProgressBar) findViewById(R.id.progressBarResetPass);

        firebaseAuth = FirebaseAuth.getInstance();
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.privacyPolicyMenuItem:
                Intent intent1 = new Intent(getApplicationContext(), PrivacyPolicy.class);
                startActivity(intent1);
                break;
            case R.id.termsCondsMenuItem:
                Intent intent2 = new Intent(getApplicationContext(), TermsAndConditions.class);
                startActivity(intent2);
                break;
            case R.id.logoutMenuItem:
                //Log.d("logout1","accessed");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("checkuser","" + user.getEmail());
                FirebaseAuth.getInstance().signOut();

                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Log.d("signout","successful");
                    Intent intent3 = new Intent(getApplicationContext(), MainActivityLogin.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent3);
                }else{
                    Log.d("signout","failed");
                    Toast.makeText(this, "Logout Failed!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return true;
        //return super.onOptionsItemSelected(item);
    }
*/


    public void requireEmail() {
        resetPasswordEmailEt.setError(getString(R.string.emailError));
        resetPasswordEmailEt.requestFocus();
    }


    public void emailFormatError() {
        resetPasswordEmailEt.setError(getString(R.string.emailCheck));
        resetPasswordEmailEt.requestFocus();
    }


    public void resetPassword(String email){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Check your email to reset your password.", Toast.LENGTH_SHORT).show();
                    progressBarResetPassword.setVisibility(View.INVISIBLE);

                    Intent intent1 = new Intent(getApplicationContext(), MainActivityLogin.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                }
                else {
                    Toast.makeText(ResetPasswordActivity.this, "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
                    progressBarResetPassword.setVisibility(View.INVISIBLE);

                    Intent intent1 = new Intent(getApplicationContext(), MainActivityLogin.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                }
            }
        });
    }

} // end class