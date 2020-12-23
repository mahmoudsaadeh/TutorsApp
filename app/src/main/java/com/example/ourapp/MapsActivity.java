package com.example.ourapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LatLng chosenLocLatLon;
    String chosenLocAddress = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        supportMapFragment();
    }

    public String getCityName(LatLng selectedLocation){
        String addressLine = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(selectedLocation.latitude, selectedLocation.longitude, 1);
            addressLine = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressLine;
    }



    public void getLocation(View view){
        if(chosenLocLatLon == null || chosenLocAddress.isEmpty()) {
            CommonMethods.makeToast(MapsActivity.this, "Please choose a location!");
        }
        else {

            String imageUri=getIntent().getStringExtra("URI");
            Intent intent = new Intent(getApplicationContext(), TeacherFormActivity.class);
            intent.putExtra("Latitude", "" + chosenLocLatLon.latitude);
            intent.putExtra("Longitude", "" + chosenLocLatLon.longitude);
            intent.putExtra("AddressLine", "" + chosenLocAddress);
            intent.putExtra("FLAG", "1");
            intent.putExtra("URI",imageUri);


            Log.d("maps", "test1");

            storeLocation();

            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(getCityName(latLng));

                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                mMap.addMarker(markerOptions);


                chosenLocLatLon = latLng;
                chosenLocAddress = getCityName(latLng);

            }
        });

    }// end onMapReady





    public void supportMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void storeLocation() {
        try {
            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("TutorData", MODE_PRIVATE, null);

            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tutorData (id VARCHAR, name VARCHAR, mail VARCHAR, age VARCHAR, address VARCHAR, subjects VARCHAR, salary VARCHAR, experience VARCHAR, phone VARCHAR, imgURI VARCHAR, location VARCHAR, longitude VARCHAR, latitude VARCHAR)");

            sqLiteDatabase.execSQL("UPDATE tutorData SET location = '" + chosenLocAddress + "', longitude = '" + chosenLocLatLon.longitude
                    + "', latitude = '" + chosenLocLatLon.latitude + "' WHERE id = '" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "'");

            Log.d("maps", "test2");

            sqLiteDatabase.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

} // end MapsActivity class