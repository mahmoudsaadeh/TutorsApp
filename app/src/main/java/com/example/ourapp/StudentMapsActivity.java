package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String tutorLat;
    private String tutorLon;
    private String tutorLoc;

    private static final String DEFAULT_LAT_OR_LON = "0";
    private static final int DELAY = 1700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        supportMapFragment();
        Intent intent = getIntent();
        String tutorId = intent.getStringExtra("tutorId");
        getInfoFromDb(tutorId);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something here
                LatLng tutorLatLng = new LatLng(Double.parseDouble(tutorLat), Double.parseDouble(tutorLon));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(tutorLatLng);
                markerOptions.title(tutorLoc);

                //mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tutorLatLng,5));
                mMap.addMarker(markerOptions);
            }
        }, DELAY);


    }


    public void supportMapFragment(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void getInfoFromDb(String tutorId){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TutorFormInfo").child(tutorId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("latitude").getValue() == null){
                    tutorLat = DEFAULT_LAT_OR_LON;
                }
                else {
                    tutorLat = snapshot.child("latitude").getValue().toString();
                }

                if(snapshot.child("longitude").getValue() == null){
                    tutorLon = DEFAULT_LAT_OR_LON;
                }
                else {
                    tutorLon = snapshot.child("longitude").getValue().toString();
                }

                if(snapshot.child("location").getValue() == null){
                    tutorLoc = DEFAULT_LAT_OR_LON;
                }
                else {
                    tutorLoc = snapshot.child("location").getValue().toString();
                }

                reference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    } // end getInfoFromDb method

} // end activity