package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityLogin extends AppCompatActivity {


    public void login (View view) {
        //Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), TeachersListActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Tutors App");

        ImageView imageView = (ImageView) findViewById(R.id.logo);
        EditText username = (EditText) findViewById(R.id.usernameEditText);
        EditText password = (EditText) findViewById(R.id.passwordEditText);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        TextView signupLink = (TextView) findViewById(R.id.signupLink);

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivitySignUp.class);
                //intent.putExtra("username","mahmoud");

                startActivity(intent);
            }
        });

    }
}