package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class StudentEditProfileActivity extends AppCompatActivity {
    String email,passwordN,passwordNC;
    EditText Email,newPassword,newPasswordC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit_profile);
        hideActionBar();
        getElementsByID();
        getUserInfoFireBase();
        setEmail();






    }

    private void hideActionBar() {  try {
        this.getSupportActionBar().hide();
    }
    catch (NullPointerException e){
        e.printStackTrace();
    }
    }

    private void getUserInfoFireBase() {
        email=FirebaseAuth.getInstance().getCurrentUser().getEmail();

    }

    private void setEmail() {
        Email.setText(email);

    }

    private void getElementsByID() {
        Email=(EditText) findViewById(R.id.emailEdit);
        newPassword=(EditText) findViewById(R.id.NewPasswordEditTextSU);
        newPasswordC=(EditText) findViewById(R.id.confirmNewPasswordET);
    }

    public void submitChanges(View view) {
        email=Email.getText().toString().trim();
        passwordN=newPassword.getText().toString().trim();
        passwordNC=newPasswordC.getText().toString().trim();


        if(CommonMethods.checkIfEmpty(email)) {
            CommonMethods.warning(Email,getString(R.string.emailError));
            return;
        }

        if(CommonMethods.isNotAnEmail(email)) {
            CommonMethods.warning(Email,getString(R.string.emailValid));
            return;
        }

        if(CommonMethods.checkIfEmpty(passwordN)) {
            CommonMethods.warning(newPassword,getString(R.string.passwordError));
            return;
        }

        if(CommonMethods.checkIfPassLengthNotValid(passwordN)) {
            CommonMethods.warning(newPassword,getString(R.string.passwordLength));
            return;
        }

        if(CommonMethods.checkIfEmpty(passwordNC)) {
            CommonMethods.warning(newPasswordC,getString(R.string.passwordConfirm));
            return;
        }
        else if(!CommonMethods.checkIfConfirmPassMatchesPass(passwordN, passwordNC)) {
            CommonMethods.warning(newPasswordC,getString(R.string.passwordMatch));
            return;
        }


        updateFireBase();


        //redirect to login screen


        SessionManagement sessionManagement=new SessionManagement(this);
        sessionManagement.removeSession();
        Intent intent = new Intent(getApplicationContext(), MainActivityLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }

    private void updateFireBase() {
        FirebaseAuth.getInstance().getCurrentUser().updateEmail(email);
        FirebaseAuth.getInstance().getCurrentUser().updatePassword(passwordN);
        CommonMethods.makeToast(StudentEditProfileActivity.this, getString(R.string.updateStudentProfile));


    }

}


