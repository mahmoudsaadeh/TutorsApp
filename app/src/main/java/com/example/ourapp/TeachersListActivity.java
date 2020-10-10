package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class TeachersListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_list);

        setTitle("TutorApp - Tutors List");

        ListView teachersListView = (ListView) findViewById(R.id.teachersListView);

        final ArrayList<String> teachersAL = new ArrayList<String>();
        for(int i=0;i<5;i++) {
            teachersAL.add("Jumana");
            teachersAL.add("Ahmad");
            teachersAL.add("Talal");
            teachersAL.add("Fadia");
            teachersAL.add("Randa");
        }

        ArrayAdapter<String> teachersAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teachersAL);

        teachersListView.setAdapter(teachersAdapter);

        teachersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(TeachersListActivity.this, teachersAL.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), TeacherInfoActivity.class);
                intent.putExtra("Teacher", teachersAL.get(position) + "");
                startActivity(intent);
            }
        });

    }
}