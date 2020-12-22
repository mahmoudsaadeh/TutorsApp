package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentSendMessage extends AppCompatActivity {


    DatabaseReference reference;
    DatabaseReference getInfo;

    EditText enterMessageET;
    TextView myMsg;

    String recipientId = "";

    String message = "";
    String getMsg = "";

    public void send(View view) {
        //store what the user entered to db
        //check if the edit text is empty

        message = enterMessageET.getText().toString();
        if(message.isEmpty()) {
            CommonMethods.makeToast(this, "Could not send an empty message!");
        }
        else{
            reference = FirebaseDatabase.getInstance().getReference().child("StudentMessage").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Recipients").child(recipientId);
            SendMessageClass sendMessageClass = new SendMessageClass("" + message);
            reference.setValue(sendMessageClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        CommonMethods.makeToast(getApplicationContext(), "Message sent successfully!");
                        myMsg.setText(message);
                        enterMessageET.setText("");
                    }
                    else {
                        CommonMethods.makeToast(getApplicationContext(), "Failed to send message!");
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_send_message);

        enterMessageET = findViewById(R.id.enterMessageET);
        myMsg = findViewById(R.id.myMsg);

        Intent intent = getIntent();
        recipientId = intent.getStringExtra("recipientId");


        // get current user(student) message
        getInfo = FirebaseDatabase.getInstance().getReference().child("StudentMessage");
        getInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
                    //Log.i("hi", "hi");
                    if(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Recipients").child(recipientId).exists()){
                        getMsg = snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Recipients").child(recipientId).child("msg").getValue().toString();
                        myMsg.setText(getMsg);
                    }
                }
                else {
                    //Log.i("hi2", "hi2");
                    myMsg.setText("My message");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("db error", error.toString());
            }
        });

    }
}