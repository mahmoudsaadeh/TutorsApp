package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class TeacherEditInfoForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_edit_info_form);

        setTitle("TutorApp - Edit Tutor Info Form");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.privacyPolicyMenuItem:
                //create an activity and write some privacy policy
                Intent intent1 = new Intent(getApplicationContext(), PrivacyPolicy.class);
                startActivity(intent1);
                break;
            case R.id.termsCondsMenuItem:
                //create an activity and write some terms and conditions
                Intent intent2 = new Intent(getApplicationContext(), TermsAndConditions.class);
                startActivity(intent2);
                break;
            case R.id.logoutMenuItem:
                //adding a verification if account was signed out successfully will make our code stronger
                FirebaseAuth.getInstance().signOut();
                Intent intent3 = new Intent(getApplicationContext(), MainActivityLogin.class);
                startActivity(intent3);
                break;
        }

        return true;
        //return super.onOptionsItemSelected(item);
    }

}