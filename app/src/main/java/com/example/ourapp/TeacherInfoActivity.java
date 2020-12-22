package com.example.ourapp;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TeacherInfoActivity extends AppCompatActivity {


    private static final int DELAY_TIME = 2000;
    private static final int DELAY_TIME_2 = 1500;
    private static final int LAYOUT_WIDTH = 1000;
    private static final int LAYOUT_HEIGHT = 1000;
    private static final float DEFAULT_PREVIOUS_STUDENT_RATE = 0;
    private static final int ONE_CHILD = 1;

    //tutor id
    String id;

    float previousStudentRate;

    DatabaseReference reference;

    DatabaseReference databaseReference;

    DatabaseReference updateData;

    DatabaseReference setRatingBarValueReference;

    RatingBar ratingBar;

    EditText teacherName;
    EditText teacherAddress;
    EditText teacherSubject;
    EditText teacherExperience;
    EditText teacherEmail;
    EditText teacherPhoneNumber;
    EditText teacherSalary;
    EditText teacherAge;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);

        setTitle("Tutor Info");

        Intent intent = getIntent();
        id = intent.getStringExtra("tutorId");

        //Log.i("ratezz", String.valueOf(previousStudentRate));

        getTeacherInfo();

        loadRating();

        changeRating();


    } //end of onCreate


    public void sendAMessage(View view) {
        Intent intent = new Intent(getApplicationContext(), StudentSendMessage.class);
        intent.putExtra("recipientId", "" + id);
        startActivity(intent);
    }


    public void ratingDialog () {

        if( PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("RATING",0)==1 ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherInfoActivity.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(ratingBar.getContext()).inflate(R.layout.rating_dialog, viewGroup, false);
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();

            alertDialog.show();
            alertDialog.setCancelable(false);
            Objects.requireNonNull(alertDialog.getWindow()).setLayout(LAYOUT_WIDTH, LAYOUT_HEIGHT); //Controlling width and height.
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something here
                    alertDialog.dismiss();
                }
            }, DELAY_TIME);
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("RATING",0).apply();
        }
    }


    public void showTeacherLocation(View view){
        Intent intent = new Intent(getApplicationContext(), StudentMapsActivity.class);
        intent.putExtra("tutorId", id);
        startActivity(intent);
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


    public void callTeacher(View v) {
        EditText teacherPhoneNumber = (EditText) findViewById(R.id.teacherPhone11);
        String phoneNumber=teacherPhoneNumber.getText().toString().substring(14);
        Intent intent=new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(intent);
    }

    public void emailTeacher(View v) {
        EditText teacherEmail = (EditText) findViewById(R.id.teacherEmail11);
        String email=teacherEmail.getText().toString().substring(7);
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }

    private void getTeacherInfo() {
        teacherName = (EditText) findViewById(R.id.teacherNameETMultiLine);
        teacherAddress = (EditText) findViewById(R.id.teacherAddressETMultiLine);
        teacherSubject = (EditText) findViewById(R.id.teacherSubjectsETMultiLine);
        teacherExperience = (EditText) findViewById(R.id.teacherExperienceETMultiLine);
        teacherEmail = (EditText) findViewById(R.id.teacherEmail11);
        teacherPhoneNumber = (EditText) findViewById(R.id.teacherPhone11);
        teacherSalary = (EditText) findViewById(R.id.editTextSalaryNumberSigned);
        ratingBar = findViewById(R.id.ratingBar);
        teacherAge = (EditText) findViewById(R.id.teacherAge);
        image= (ImageView)  findViewById(R.id.image);

        reference= FirebaseDatabase.getInstance().getReference("TutorFormInfo");

        /*reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
        });*/

        reference.addValueEventListener(new ValueEventListener() {
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
                teacherEmail.setText(Html.fromHtml("Email:<font color='#800080'> "+email+" </font>"));
                teacherPhoneNumber.setText(Html.fromHtml("Phone Number:<font color='#800080'> "+phone+"</font>"));
                teacherSalary.setText("Salary: "+salary+" $/hour");
                Picasso.get().load(imageUrl).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void loadRating() {
        setRatingBarValueReference = FirebaseDatabase.getInstance().getReference("TutorsRating");

        setRatingBarValueReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                    Log.i("1","1");

                    if(snapshot.child(id).exists()) {

                        Log.i("2","2");

                        if(snapshot.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                            previousStudentRate = Float.parseFloat(snapshot.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue().toString());
                            Log.i("3", "3");
                            Log.i("4", "" + previousStudentRate);
                        }
                    }
                }
                else {
                    previousStudentRate = DEFAULT_PREVIOUS_STUDENT_RATE;
                }
                reference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        Log.i("rate55", "" + previousStudentRate);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something here
                ratingBar.setRating(previousStudentRate);
                Log.i("rate66", "" + previousStudentRate);
            }
        }, DELAY_TIME_2);

    } // end loadRating



    @SuppressLint("ClickableViewAccessibility")
    public void changeRating(){
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("RATING",1).apply();

                }

                return false;
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("TutorsRating");


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, final float rating, boolean fromUser) {






                //Toast.makeText(TeacherInfoActivity.this, "" + ratingBar.getRating(), Toast.LENGTH_SHORT).show();

                // freeze ratingbar (disable it)
                //ratingBar.setIsIndicator(true);

                /*float newRating = (ratingBar.getRating()+Float.parseFloat(oldRating))/(float) 2;
                studentRating = String.valueOf(newRating);*/

                //Log.i("myRate", String.valueOf(rating));

                updateData = FirebaseDatabase.getInstance().getReference("TutorsRating");

                updateData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //if tutorId child already exists in TutorsRating parent(if the teacher was rated previously by one of the students)
                        if(snapshot.child(id).exists()) {
                            //if the logged-in student already rated his teacher, update the existing rating of both student & teacher
                            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                                if(snapshot.child(id).child("ratedBy").getChildrenCount() > 1) {
                                    if (snapshot.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                                        String previousStudentRate = snapshot.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue().toString();
                                        String currentTutorRate = snapshot.child(id).child("rating").getValue().toString();
                                        //removed the effect of the previous rating the student already did
                                        String preCurrentTutorRate = String.valueOf((Float.parseFloat(currentTutorRate) * (float) 2) - Float.parseFloat(previousStudentRate));
                                        String newTutorRate = String.valueOf((Float.parseFloat(preCurrentTutorRate) + rating) / (float) 2);

                                        databaseReference.child(id).child("rating").setValue(newTutorRate);
                                        databaseReference.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(String.valueOf(rating)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                ratingDialog();
                                            }
                                        });

                                        //finalTutorRate = newTutorRate;
                                        updateData.removeEventListener(this);
                                    }
                                    else {
                                        String currentTutorRate = snapshot.child(id).child("rating").getValue().toString();
                                        String newTutorRate = String.valueOf((Float.parseFloat(currentTutorRate) + rating) / (float) 2);

                                        databaseReference.child(id).child("rating").setValue(newTutorRate);
                                        databaseReference.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(String.valueOf(rating)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                ratingDialog();
                                            }
                                        });

                                        //finalTutorRate = newTutorRate;
                                        updateData.removeEventListener(this);
                                    }
                                }
                                else {
                                    if(snapshot.child(id).child("ratedBy").getChildrenCount() == ONE_CHILD) {
                                        //if this single student is the same that rated the tutor before
                                        if(snapshot.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
                                            databaseReference.child(id).child("rating").setValue(String.valueOf(rating));
                                            databaseReference.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(String.valueOf(rating)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    ratingDialog();
                                                }
                                            });

                                            //finalTutorRate = String.valueOf(rating);
                                            updateData.removeEventListener(this);
                                        }
                                        //else, add the new student
                                        else {
                                            String currentTutorRate = snapshot.child(id).child("rating").getValue().toString();
                                            String newTutorRate = String.valueOf((Float.parseFloat(currentTutorRate) + rating) / (float) 2);

                                            databaseReference.child(id).child("rating").setValue(newTutorRate);
                                            databaseReference.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(String.valueOf(rating)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    ratingDialog();
                                                }
                                            });

                                            //finalTutorRate = newTutorRate;
                                            updateData.removeEventListener(this);
                                        }
                                    }
                                }
                            }
                        }
                        //in this case, the tutor wasn't rated previously by any of the students
                        else {
                            databaseReference.child(id).child("rating").setValue(String.valueOf(rating));
                            databaseReference.child(id).child("ratedBy").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(String.valueOf(rating)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ratingDialog();
                                }
                            });

                            //finalTutorRate = String.valueOf(rating);
                            updateData.removeEventListener(this);
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
    }


}//end class (activity)