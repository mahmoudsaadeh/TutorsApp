package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeachersListActivity extends AppCompatActivity {

    //private static final String TAG = "TeachersListActivity";

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    private ArrayList<String> tutorsIds = new ArrayList<>();

    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;

    SwipeRefreshLayout swipeRefreshLayout;

    //String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_list);

        //Intent intent = getIntent();
        //currentUser = intent.getStringExtra("username");

        setTitle("" + MainActivityLogin.un);

        //tutorsIds.clear();

        //Log.i("rate", TeacherInfoActivity.finalTutorRate);

        databaseReference = FirebaseDatabase.getInstance().getReference("TutorFormInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 1;
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    tutorsIds.add(postSnapshot.getKey());
                    Log.i("tutorsId" + i, postSnapshot.getKey());
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TeachersListActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //a delay was added because it's taking time to get tutors' ids from db
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something here
                initializeImageBitmaps();
            }
        }, 2000);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //restart this activity to reload data
                Intent intent = new Intent(getApplicationContext(), TeachersListActivity.class);
                startActivity(intent);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }





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
                    SessionManagement sessionManagement=new SessionManagement(TeachersListActivity.this);
                    sessionManagement.removeSession();
                    Intent intent3 = new Intent(getApplicationContext(), MainActivityLogin.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent3);
                }else{
                    Log.d("signout","failed");
                    Toast.makeText(this, "Logout Failed!", Toast.LENGTH_SHORT).show();
                }

                tutorsIds.clear();

                break;
        }
        return true;
        //return super.onOptionsItemSelected(item);
    }






    private void initializeImageBitmaps(){
        Log.d("initializeBitmapFunc", "preparing bitmaps");
/*
        mImageUrls.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        mNames.add("Havasu Falls");

        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mNames.add("Trondheim");

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mNames.add("Portugal");

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
        mNames.add("Rocky Mountain National Park");


        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mNames.add("Mahahual");

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        mNames.add("Frozen Lake");


        mImageUrls.add("https://i.redd.it/glin0nwndo501.jpg");
        mNames.add("White Sands Desert");

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");
        mNames.add("Austrailia");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        mNames.add("Washington");
*/

        Log.d("tutirIdAL size", tutorsIds.size() + "");
/*
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("TutorFormInfo").child(tutorsIds.get(0));
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("childrenCount", "" + snapshot.getChildrenCount());//12
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
*/

        for(int k = 0;k<tutorsIds.size();k++){
            //databaseReference2 = FirebaseDatabase.getInstance().getReference().child(tutorsIds.get(0));
            databaseReference2 = FirebaseDatabase.getInstance().getReference().child("TutorFormInfo").child(tutorsIds.get(k));
            //Log.d("loop1", "for loop 1");
            databaseReference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Log.d("outsideLoop2", "outside loop2");
                    int n=0;

                    if(snapshot.hasChildren()){
                        Log.d("hasChildren", "yes");
                    }
                    if(snapshot.exists()){
                        Log.d("snapshot", "exists");
                    }
                    //Log.d("childrenCount", "" + snapshot.getChildrenCount());//0


                    mNames.add(snapshot.child("name").getValue().toString());
                    mImageUrls.add(snapshot.child("imageUrl").getValue().toString());

                    /*Log.i("tname" + n, mNames.get(n));
                    Log.i("tnamez" + n, snapshot.child("name").getValue().toString());
                    Log.i("turl" + n, mImageUrls.get(n));
                    Log.i("turlz" + n, snapshot.child("imageUrl").getValue().toString());*/

                    n++;

                    /*for(DataSnapshot postSnapshot : snapshot.getChildren()){
                        Log.d("loop2", "fot loop 2");

                        mNames.add(postSnapshot.child("name").getValue().toString());
                        mImageUrls.add(postSnapshot.child("imageUrl").getValue().toString());
                        Log.i("tname" + n, mNames.get(n));
                        Log.i("tnamez" + n, postSnapshot.child("name").getValue().toString());
                        Log.i("turl" + n, mImageUrls.get(n));
                        Log.i("turlz" + n, postSnapshot.child("imageUrl").getValue().toString());
                    }*/
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(TeachersListActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something here
                initRecyclerView();
            }
        }, 1500);

        //initRecyclerView();

    }

    private void initRecyclerView(){
        //Log.d("initRecView", "initRecyclerView: initializing staggered recyclerview.");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(mNames,mImageUrls,this, tutorsIds);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

        //Log.d("AL size", tutorsIds.size() + "");
    }

}