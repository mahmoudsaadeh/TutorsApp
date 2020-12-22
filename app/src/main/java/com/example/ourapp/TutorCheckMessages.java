package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TutorCheckMessages extends AppCompatActivity {

    DatabaseReference reference;

    TextView fromContent;
    TextView studentMsgContent;
    TextView studentMail;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_check_messages);

        fromContent = findViewById(R.id.fromContent);
        studentMsgContent = findViewById(R.id.studentMessageContent);
        studentMail = findViewById(R.id.studentMailContent);

        linearLayout = findViewById(R.id.linearLayout);

        //get real data from db

        ArrayList<String> test = new ArrayList<>();
        test.add("1");
        test.add("2");
        test.add("3");
        test.add("4");
        test.add("5");

        for( int i = 0; i < test.size(); i++ )
        {
            TextView textView1 = new TextView(this);
            //set width and height to wrap_content
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params1.gravity = Gravity.LEFT;
            params1.setMargins(0, 0, 0, 15);
            textView1.setLayoutParams(params1);
            textView1.setText("From:");
            linearLayout.addView(textView1);


            TextView fromTV = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            params.setMargins(0, 0, 0, 15);
            fromTV.setLayoutParams(params);
            fromTV.setText("Sender" + test.get(i));
            linearLayout.addView(fromTV);


            TextView textView2 = new TextView(this);
            //set width and height to wrap_content
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params2.gravity = Gravity.LEFT;
            params2.setMargins(0, 0, 0, 15);
            textView2.setLayoutParams(params2);
            textView2.setText("Student message:");
            linearLayout.addView(textView2);


            TextView msgTV = new TextView(this);
            LinearLayout.LayoutParams paramsMsg = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsMsg.gravity = Gravity.RIGHT;
            paramsMsg.setMargins(0, 0, 0, 15);
            msgTV.setLayoutParams(params);
            msgTV.setText("Message Content");
            linearLayout.addView(msgTV);


            TextView textView3 = new TextView(this);
            //set width and height to wrap_content
            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params3.gravity = Gravity.LEFT;
            params3.setMargins(0, 0, 0, 25);
            textView3.setLayoutParams(params3);
            textView3.setText("Student email:");
            linearLayout.addView(textView3);


            TextView mailTV = new TextView(this);
            LinearLayout.LayoutParams paramsMail = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsMail.gravity = Gravity.RIGHT;
            paramsMail.setMargins(0, 0, 0, 27);
            mailTV.setLayoutParams(params);
            mailTV.setText("Email");
            linearLayout.addView(mailTV);


            View view = new View(this);
            LinearLayout.LayoutParams paramsLineBreak = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5);
            paramsLineBreak.setMargins(0,0,0, 17);
            view.setLayoutParams(paramsLineBreak);
            view.setBackgroundColor(getColor(android.R.color.black));
            linearLayout.addView(view);

        }

    }//end onCreate
}// end Activity