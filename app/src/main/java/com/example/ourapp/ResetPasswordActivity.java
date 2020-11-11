package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText resetPasswordEmailEt;
    private Button resetPasswordButton;
    //private ProgressBar progressBarResetPassword;
    private ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;

    public void resetPassword (View view){
        String email = resetPasswordEmailEt.getText().toString().trim();

        if(CommonMethods.checkIfEmpty(email)){
            //username is the email, didn't rename because it's causing trouble
            //resetPasswordEmailEt.setError("Email is required!");
            CommonMethods.warning(resetPasswordEmailEt,getString(R.string.emailError));
            return;
        }

        if(CommonMethods.isNotAnEmail(email)){
            //resetPasswordEmailEt.setError("Please enter a valid email!");
            CommonMethods.warning(resetPasswordEmailEt,getString(R.string.emailValid));
            return;
        }

        //progressBarResetPassword.setVisibility(View.VISIBLE);
        CommonMethods.displayLoadingScreen(progressDialog);

        resetPassword(email);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetPasswordEmailEt = (EditText) findViewById(R.id.resetPassEmailET);
        resetPasswordButton = (Button) findViewById(R.id.resetPasswordButton);
        //progressBarResetPassword = (ProgressBar) findViewById(R.id.progressBarResetPass);
        progressDialog = new ProgressDialog(ResetPasswordActivity.this);

        firebaseAuth = FirebaseAuth.getInstance();
    }



    public void resetPassword(String email){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Check your email to reset your password.", Toast.LENGTH_SHORT).show();
                    //progressBarResetPassword.setVisibility(View.INVISIBLE);
                    progressDialog.dismiss();

                    Intent intent1 = new Intent(getApplicationContext(), MainActivityLogin.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                }
                else {
                    Toast.makeText(ResetPasswordActivity.this, "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
                    //progressBarResetPassword.setVisibility(View.INVISIBLE);
                    progressDialog.dismiss();

                    Intent intent1 = new Intent(getApplicationContext(), MainActivityLogin.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                }
            }
        });
    }

} // end class