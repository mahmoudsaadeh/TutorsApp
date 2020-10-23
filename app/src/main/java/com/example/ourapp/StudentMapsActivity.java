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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent intent = getIntent();
        String tutorId = intent.getStringExtra("tutorId");

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TutorFormInfo").child(tutorId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("latitude").getValue() == null){
                    tutorLat = "0";
                }
                else {
                    tutorLat = snapshot.child("latitude").getValue().toString();
                }

                if(snapshot.child("longitude").getValue() == null){
                    tutorLon = "0";
                }
                else {
                    tutorLon = snapshot.child("longitude").getValue().toString();
                }

                if(snapshot.child("location").getValue() == null){
                    tutorLoc = "0";
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

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/


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
        }, 1700);


    }
}