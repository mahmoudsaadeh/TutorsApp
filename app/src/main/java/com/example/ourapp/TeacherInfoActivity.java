package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

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

        teacherName.setText("Name: " + getIntent().getStringExtra("Teacher"));
        teacherAddress.setText("Address: Jounieh, Street 55, Lebanon");
        teacherSubject.setText("Subject: Math, physics, chemistry");
        teacherExperience.setText("Experience: BS in math and phy and chem - taught in 4 schools, 2 private and 2 public" +
                " - 5 yrs in teaching");
        teacherEmail.setText("Email: test@test.com");
        teacherPhoneNumber.setText("Phone Number: +961-78-888999");
        teacherSalary.setText("Salary: 50 USD / hr");

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(TeacherInfoActivity.this, "" + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}