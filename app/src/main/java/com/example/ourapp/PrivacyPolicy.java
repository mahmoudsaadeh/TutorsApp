package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class PrivacyPolicy extends AppCompatActivity {

    //https://app-privacy-policy-generator.firebaseapp.com/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        setTitle("TutorApp - Privacy Policy");

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
                FirebaseAuth.getInstance().signOut();
                Intent intent3 = new Intent(getApplicationContext(), MainActivityLogin.class);
                startActivity(intent3);
                break;
        }

        return true;
        //return super.onOptionsItemSelected(item);
    }


}