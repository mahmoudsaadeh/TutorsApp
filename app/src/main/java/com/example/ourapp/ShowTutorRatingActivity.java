package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowTutorRatingActivity extends AppCompatActivity {
    TextView ratingText;
    RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tutor_rating);
        hideActionBar();
        ratingText=(TextView) findViewById(R.id.ratingValue);
        ratingBar=(RatingBar) findViewById(R.id.tRatingBar);
        getTutorRating();

    }

    public void hideActionBar(){
        try {
        this.getSupportActionBar().hide();
    }
    catch (NullPointerException e) {
        e.printStackTrace();
    }
    }

    public void getTutorRating() {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("TutorsRating");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String rating=snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rating").getValue(String.class);
                displayRating(rating);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    public void displayRating(String rating){

        ratingText.setText(rating);
        ratingBar.setRating(Float.parseFloat(rating));
        ratingBar.setIsIndicator(true);

    }
}