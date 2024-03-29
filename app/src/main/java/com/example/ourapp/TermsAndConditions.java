package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TermsAndConditions extends AppCompatActivity {

    //private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        setTitle("TutorApp - Terms & Conditions");

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.showRating).setVisible(false);
        menu.findItem(R.id.editProfileMenuItem).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.editProfileMenuItem:
                Intent intent1 = new Intent(getApplicationContext(), StudentEditProfileActivity.class);
                startActivity(intent1);
                break;
            case R.id.privacyPolicyMenuItem:
                Intent intent2 = new Intent(getApplicationContext(), PrivacyPolicy.class);
                startActivity(intent2);
                break;
            case R.id.termsCondsMenuItem:
                Intent intent3 = new Intent(getApplicationContext(), TermsAndConditions.class);
                startActivity(intent3);
                break;
            case R.id.logoutMenuItem:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("checkuser","" + user.getEmail());
                FirebaseAuth.getInstance().signOut();

                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Log.d("signout","successful");
                    SessionManagement sessionManagement=new SessionManagement(TermsAndConditions.this);
                    sessionManagement.removeSession();
                    Intent intent4 = new Intent(getApplicationContext(), MainActivityLogin.class);
                    intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    try {
                        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("TutorData", MODE_PRIVATE, null);
                        sqLiteDatabase.execSQL("DELETE FROM tutorData");
                        sqLiteDatabase.close();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    startActivity(intent4);
                }else{
                    Log.d("signout","failed");
                    Toast.makeText(this, "Logout Failed!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return true;
    }

}