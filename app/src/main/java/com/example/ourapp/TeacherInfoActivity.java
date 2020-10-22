package com.example.ourapp;
import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherInfoActivity extends AppCompatActivity {


    //String id="BN6PPyaVlGf6fJ2V8wCxwmxEaMf2"; //id of the clicked teacher
    //String oldRating;

    //tutor id
    String id;

    //public static String finalTutorRate = "0";

    DatabaseReference databaseReference;

    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);

        setTitle("TutorApp - Tutor Info");

        Intent intent = getIntent();
        id = intent.getStringExtra("tutorId");

        final EditText teacherName = (EditText) findViewById(R.id.teacherNameETMultiLine);
        final EditText teacherAddress = (EditText) findViewById(R.id.teacherAddressETMultiLine);
        final EditText teacherSubject = (EditText) findViewById(R.id.teacherSubjectsETMultiLine);
        final EditText teacherExperience = (EditText) findViewById(R.id.teacherExperienceETMultiLine);
        final EditText teacherEmail = (EditText) findViewById(R.id.teacherEmail11);
        final EditText teacherPhoneNumber = (EditText) findViewById(R.id.teacherPhone11);
        final EditText teacherSalary = (EditText) findViewById(R.id.editTextSalaryNumberSigned);
        ratingBar = findViewById(R.id.ratingBar);
        final EditText teacherAge = (EditText) findViewById(R.id.teacherAge);
        final ImageView image=(ImageView)  findViewById(R.id.image);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("TutorFormInfo");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name=snapshot.child(id).child("name").getValue(String.class);
                        String address=snapshot.child(id).child("address").getValue(String.class);
                        String exp=snapshot.child(id).child("experience").getValue(String.class);
                        String email=snapshot.child(id).child("email").getValue(String.class);
                        String phone=snapshot.child(id).child("phoneNum").getValue(String.class);
                        String salary=snapshot.child(id).child("salary").getValue(String.class);
                        String age=snapshot.child(id).child("age").getValue(String.class);
                        String imageUrl=snapshot.child(id).child("imageUrl").getValue(String.class);
                        String subject=snapshot.child(id).child("subject").getValue(String.class);


                        teacherName.setText("Name: "+name);
                        teacherAddress.setText("Address: "+address);
                        teacherAge.setText("Age: "+age );
                        teacherSubject.setText("Subject: "+subject);
                        teacherExperience.setText("Experience: "+exp);
                        teacherEmail.setText(Html.fromHtml("Email:<font color='purple'> "+email+" </font>"));
                        teacherPhoneNumber.setText(Html.fromHtml("Phone Number:<font color='purple'> "+phone+"</font>"));
                        teacherSalary.setText("Salary: "+salary+" $/hour");
                        Picasso.get().load(imageUrl).into(image);


                    }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {

                    }
                    });


        databaseReference = FirebaseDatabase.getInstance().getReference("TutorsRating");


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                Toast.makeText(TeacherInfoActivity.this, "" + ratingBar.getRating(), Toast.LENGTH_SHORT).show();

                // freeze ratingbar (disable it)
                //ratingBar.setIsIndicator(true);

                /*float newRating = (ratingBar.getRating()+Float.parseFloat(oldRating))/(float) 2;
                studentRating = String.valueOf(newRating);*/

                //Log.i("myRate", String.valueOf(rating));

                //final float newRating = 0;

                final DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("TutorsRating");

                updateData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //if tutorId child already exists in TutorsRating parent(if the teacher was rated previously by one of the students)
                        if(snapshot.child(id).exists()) {
                            //if the logged-in student already rated his teacher, update the existing rating of both student & teacher
                            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                                if (snapshot.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                                    String previousStudentRate = snapshot.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue().toString();
                                    String currentTutorRate = snapshot.child(id).child("rating").getValue().toString();
                                    //removed the effect of the previous rating the student already did
                                    String preCurrentTutorRate = String.valueOf((Float.parseFloat(currentTutorRate) * (float) 2) - Float.parseFloat(previousStudentRate));
                                    String newTutorRate = String.valueOf((Float.parseFloat(preCurrentTutorRate) + rating) / (float) 2);

                                    databaseReference.child(id).child("rating").setValue(newTutorRate);
                                    databaseReference.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(String.valueOf(rating));

                                    //finalTutorRate = newTutorRate;
                                }
                                else {
                                    String currentTutorRate = snapshot.child(id).child("rating").getValue().toString();
                                    String newTutorRate = String.valueOf((Float.parseFloat(currentTutorRate) + rating) / (float) 2);

                                    databaseReference.child(id).child("rating").setValue(newTutorRate);
                                    databaseReference.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(String.valueOf(rating));

                                    //finalTutorRate = newTutorRate;
                                }
                            }
                        }
                        //in this case, the tutor wasn't rated previously by any of the students
                        else {
                            databaseReference.child(id).child("rating").setValue(String.valueOf(rating));
                            databaseReference.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(String.valueOf(rating));

                            //finalTutorRate = String.valueOf(rating);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i("db error", error.getMessage());
                    }
                });

                //ratingBar.setIsIndicator(true);

            }
        });//end setOnRatingBarChangeListener


    }//end of onCreate


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
                    SessionManagement sessionManagement=new SessionManagement(TeacherInfoActivity.this);
                    sessionManagement.removeSession();
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


}//end class (activity)