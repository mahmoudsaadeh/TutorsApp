package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivitySignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sign_up);

        ImageView imageView = (ImageView) findViewById(R.id.logo);
        EditText username = (EditText) findViewById(R.id.usernameETSU);
        EditText password = (EditText) findViewById(R.id.passwordEditTextSU);
        Button signupAsStudentButton = (Button) findViewById(R.id.signupAsStudentBtn);
        Button signupAsTeacherButton = (Button) findViewById(R.id.signupAsTeacherBtn);
        TextView loginLink = (TextView) findViewById(R.id.loginLink);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivityLogin.class);
                //intent.putExtra("username","mahmoud");

                startActivity(intent);
            }
        });

    }
}