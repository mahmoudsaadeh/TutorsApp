package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class TutorCheckMessages extends AppCompatActivity {

    DatabaseReference reference;
    DatabaseReference reference2;

    /*TextView fromContent;
    TextView studentMsgContent;
    TextView studentMail;*/

    LinearLayout linearLayout;

    String fromId = "";
    String fromUsername = "";
    String message = "";
    String mail = "";

    ArrayList<String> from = new ArrayList<>();
    ArrayList<String> fromIds = new ArrayList<>();
    ArrayList<String> msg = new ArrayList<>();
    ArrayList<String> email = new ArrayList<>();

    int j = 0;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_check_messages);

        /*fromContent = findViewById(R.id.fromContent);
        studentMsgContent = findViewById(R.id.studentMessageContent);
        studentMail = findViewById(R.id.studentMailContent);*/

        linearLayout = findViewById(R.id.linearLayout);

        progressDialog = new ProgressDialog(TutorCheckMessages.this);

        //get real data from db

        msg.clear();
        email.clear();
        from.clear();
        fromIds.clear();

        CommonMethods.displayLoadingScreen(progressDialog);

        reference = FirebaseDatabase.getInstance().getReference().child("StudentMessage");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    Log.i("1", "1");
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if(snapshot1.child("Recipients").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                            Log.i("2", "2");

                            message = snapshot1.child("Recipients").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("msg").getValue().toString();
                            fromId = snapshot1.getKey();

                            msg.add(message);
                            fromIds.add(fromId);
                        }
                    }
                }
                //reference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("msg size", msg.size() + "");
                Log.i("fromIds size", fromIds.size() + "");

                if(msg.size() == fromIds.size()){
                    Log.i("checkarrays", "equal");

                    //for(j=0; j<fromIds.size();j++) {
                        Log.i("d0", "yes");
                        reference2 = FirebaseDatabase.getInstance().getReference().child("User");
                        Log.i("j outside", "" + j);

                        reference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.i("d1", "yes");
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Log.i("d2", "yes");
                                    Log.i("j inside", "" + j);
                                    Log.i("snapshot1 key", "" + snapshot1.getKey());


                                    if(j < fromIds.size()) {
                                        Log.i("al key", "" + fromIds.get(j));

                                        if(snapshot1.getKey().equals(fromIds.get(j))) {
                                            Log.i("d3", "yes");

                                            fromUsername = snapshot1.child("username").getValue().toString();
                                            mail = snapshot1.child("email").getValue().toString();

                                            from.add(fromUsername);
                                            email.add(mail);

                                            j++;
                                        }
                                    }
                                    else {
                                        break;
                                    }
                                }
                                //done2.countDown();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    //}
                }
                else{
                    Log.i("checkarrays", "not equal");
                }
            }
        }, 4000);





        // wait the specified time, then run the code inside run()
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.i("3_", "3_");

                if(from.size() > 0 && msg.size() > 0 && email.size() > 0){
                    for( int i = 0; i < msg.size(); i++ )  {
                        Log.i("4", "4");

                        Log.i("fromSize", "" + from.size());
                        Log.i("emailSize", "" + email.size());

                        TextView textView1 = new TextView(getApplicationContext());
                        //set width and height to wrap_content
                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params1.gravity = Gravity.LEFT;
                        params1.setMargins(0, 0, 0, 15);
                        textView1.setLayoutParams(params1);
                        textView1.setText("From:");
                        linearLayout.addView(textView1);


                        TextView fromTV = new TextView(getApplicationContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.RIGHT;
                        params.setMargins(0, 0, 0, 15);
                        fromTV.setLayoutParams(params);
                        fromTV.setText("" + from.get(i));
                        linearLayout.addView(fromTV);


                        TextView textView2 = new TextView(getApplicationContext());
                        //set width and height to wrap_content
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params2.gravity = Gravity.LEFT;
                        params2.setMargins(0, 0, 0, 15);
                        textView2.setLayoutParams(params2);
                        textView2.setText("Student message:");
                        linearLayout.addView(textView2);


                        TextView msgTV = new TextView(getApplicationContext());
                        LinearLayout.LayoutParams paramsMsg = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        paramsMsg.gravity = Gravity.RIGHT;
                        paramsMsg.setMargins(0, 0, 0, 15);
                        msgTV.setLayoutParams(params);
                        msgTV.setText("" + msg.get(i));
                        linearLayout.addView(msgTV);


                        TextView textView3 = new TextView(getApplicationContext());
                        //set width and height to wrap_content
                        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params3.gravity = Gravity.LEFT;
                        params3.setMargins(0, 0, 0, 25);
                        textView3.setLayoutParams(params3);
                        textView3.setText("Student email:");
                        linearLayout.addView(textView3);


                        TextView mailTV = new TextView(getApplicationContext());
                        LinearLayout.LayoutParams paramsMail = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        paramsMail.gravity = Gravity.RIGHT;
                        paramsMail.setMargins(0, 0, 0, 27);
                        mailTV.setLayoutParams(params);
                        final String temp = email.get(i);
                        //mailTV.setText(temp);
                        mailTV.setText(Html.fromHtml("<font color='#800080'>" + email.get(i) + "</font>"));

                        //emailStudent(mailTV, email.get(i));
                        mailTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                emailStudent(v, temp);
                            }
                        });

                        linearLayout.addView(mailTV);


                        View view = new View(getApplicationContext());
                        LinearLayout.LayoutParams paramsLineBreak = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5);
                        paramsLineBreak.setMargins(0,0,0, 17);
                        view.setLayoutParams(paramsLineBreak);
                        view.setBackgroundColor(getColor(android.R.color.black));
                        linearLayout.addView(view);

                    }// end for loop
                }
                else {
                    TextView textViewz = new TextView(getApplicationContext());
                    //set width and height to wrap_content
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params2.gravity = Gravity.CENTER;
                    //params2.setMargins(0, 0, 0, 15);
                    textViewz.setLayoutParams(params2);
                    textViewz.setText("No messages to show!");
                    textViewz.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    linearLayout.addView(textViewz);
                }



            }
        }, 7000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if ((progressDialog != null) && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (final Exception e) {
                    // Handle or log or ignore
                    e.printStackTrace();
                } finally {
                    progressDialog = null;
                }
            }
        }, 8000);


        /*ArrayList<String> test = new ArrayList<>();
        test.add("1");
        test.add("2");
        test.add("3");
        test.add("4");
        test.add("5");*/





    }//end onCreate

    public void emailStudent(View v, String emaill) {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { emaill });
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }

}// end Activity