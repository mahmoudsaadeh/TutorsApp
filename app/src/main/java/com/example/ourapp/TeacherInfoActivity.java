package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TeacherInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);

        setTitle("TutorApp - Tutor Info");

        EditText teacherName = (EditText) findViewById(R.id.teacherNameETMultiLine);
        EditText teacherAddress = (EditText) findViewById(R.id.teacherAddressETMultiLine);
        EditText teacherSubject = (EditText) findViewById(R.id.teacherSubjectsETMultiLine);
        EditText teacherExperience = (EditText) findViewById(R.id.teacherExperienceETMultiLine);
        EditText teacherEmail = (EditText) findViewById(R.id.teacherEmail11);
        EditText teacherPhoneNumber = (EditText) findViewById(R.id.teacherPhone11);
        EditText teacherSalary = (EditText) findViewById(R.id.editTextSalaryNumberSigned);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        EditText teacherAge = (EditText) findViewById(R.id.teacherAge);
        //teacherName.setText("Name: " + getIntent().getStringExtra("Teacher"));
        teacherName.setText("Name: Talal Dan");
        teacherAddress.setText("Address: Jounieh, Street 55, Lebanon");
        teacherAge.setText("Age: 31");
        teacherSubject.setText("Subject: Math, physics, chemistry");
        teacherExperience.setText("Experience: BS in math and phy and chem - taught in 4 schools, 2 private and 2 public" +
                " - 5 yrs in teaching");
        teacherEmail.setText(Html.fromHtml("Email:<font color='purple'> test@test.com </font>"));



        teacherPhoneNumber.setText(Html.fromHtml("Phone Number:<font color='purple'> +961-78-888999</font>"));
        teacherSalary.setText("Salary: 50 USD / hr");

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(TeacherInfoActivity.this, "" + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
            }
        });

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


    public void callTeacher(View v){
        EditText teacherPhoneNumber = (EditText) findViewById(R.id.teacherPhone11);
        String phoneNumber=teacherPhoneNumber.getText().toString().substring(14);
        Intent intent=new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(intent);
    }
    public void emailTeacher(View v){
        EditText teacherEmail = (EditText) findViewById(R.id.teacherEmail11);
        String email=teacherEmail.getText().toString().substring(7);
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }
}