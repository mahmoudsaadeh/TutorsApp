package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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
import java.lang.reflect.Method;
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

    private static final int DELAY = 2000;
    private static final int DELAY_2 = 1500;
    ProgressDialog progressDialog ;

    RecyclerViewAdapter recyclerViewAdapter;

    //String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_list);
        progressDialog = new ProgressDialog(TeachersListActivity.this);
        CommonMethods.displayLoadingScreen(progressDialog);
        String s=MainActivityLogin.un;
        setTitle("Welcome " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("username", "NULL"));



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
                CommonMethods.makeToast(TeachersListActivity.this, "" + error.getMessage());
            }
        });


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeImageBitmaps();
            }
        }, DELAY);


        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(getApplicationContext(), TeachersListActivity.class);
                startActivity(intent);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.showRating).setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.editProfileMenuItem:
                Intent intent1 = new Intent(getApplicationContext(), StudentEditProfileActivity.class);
                startActivity(intent1);
                break;
            case R.id.privacyPolicyMenuItem:
                Intent intent2 = new Intent(getApplicationContext(), PrivacyPolicy.class);
                startActivity(intent2);
                break;
            case R.id.termsCondsMenuItem:
                Intent intent3 = new Intent(getApplicationContext(), TermsAndConditions.class);
                startActivity(intent3);
                break;
            case R.id.logoutMenuItem:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("checkuser","" + user.getEmail());
                FirebaseAuth.getInstance().signOut();

                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Log.d("signout","successful");
                    SessionManagement sessionManagement=new SessionManagement(TeachersListActivity.this);
                    sessionManagement.removeSession();
                    Intent intent4 = new Intent(getApplicationContext(), MainActivityLogin.class);
                    intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent4);
                }else {
                    Log.d("signout","failed");
                    Toast.makeText(this, "Logout Failed!", Toast.LENGTH_SHORT).show();
                }

                tutorsIds.clear();

                break;
        }
        return true;
        //return super.onOptionsItemSelected(item);
    }






    private void initializeImageBitmaps() {
        Log.d("initializeBitmapFunc", "preparing bitmaps");

        Log.d("tutirIdAL size", tutorsIds.size() + "");

        for(int k = 0; k < tutorsIds.size(); k++) {

            databaseReference2 = FirebaseDatabase.getInstance().getReference().child("TutorFormInfo").child(tutorsIds.get(k));
            databaseReference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int n = 0;

                    if(snapshot.hasChildren()){
                        Log.d("hasChildren", "yes");
                    }
                    if(snapshot.exists()){
                        Log.d("snapshot", "exists");
                    }


                    mNames.add(snapshot.child("name").getValue().toString());
                    mImageUrls.add(snapshot.child("imageUrl").getValue().toString());


                    n++;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    CommonMethods.makeToast(TeachersListActivity.this, "" + error.getMessage());
                }
            });
        }


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something here
                initRecyclerView();
            }
        }, DELAY_2);


    }

    private void initRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(mNames,mImageUrls,this, tutorsIds);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);


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



}