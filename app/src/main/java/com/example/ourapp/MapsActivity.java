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
            //Log.i("address line: ", addresses.get(0).getAddressLine(0) + "");
            /*Log.i("Locality: ", addresses.get(0).getLocality() + "");
            Log.i("country name: ", addresses.get(0).getCountryName() + "");
            Log.i("Locale: ", addresses.get(0).getLocale() + "");*/
            //return city;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressLine;
    }



    public void getLocation(View view){
        //Log.d("getLoc1","accessed");
        if(chosenLocLatLon == null || chosenLocAddress.isEmpty()) {
            Toast.makeText(this, "Please choose a location!", Toast.LENGTH_SHORT).show();
        }
        else {
            //Log.d("getLoc2","accessed");
            Intent intent = new Intent(getApplicationContext(), TeacherFormActivity.class);
            intent.putExtra("Latitude", "" + chosenLocLatLon.latitude);
            intent.putExtra("Longitude", "" + chosenLocLatLon.longitude);
            intent.putExtra("AddressLine", "" + chosenLocAddress);
            intent.putExtra("FLAG", "1");
            //Log.d("lat","" + chosenLocLatLon.latitude);
            //Log.d("lon","" + chosenLocLatLon.longitude);

            Log.d("maps", "test1");

            storeLocation();

            startActivity(intent);
            finish();
        }
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
        //LatLng sydney = new LatLng(-34, 151);
        /*mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(getCityName(latLng));

                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                mMap.addMarker(markerOptions);

                //getLocation(latLng, getCityName(latLng));

                chosenLocLatLon = latLng;
                chosenLocAddress = getCityName(latLng);

            }
        });

    }// end onMapReady


    //hadi
    /*
    public void getLocation(View view){
        double latitude=mMap.getMyLocation().getLatitude();
        double longitude=mMap.getMyLocation().getLongitude();
        Intent intent = new Intent(MapsActivity.this, TeacherFormActivity.class);
        intent.putExtra("lat", latitude);
        intent.putExtra("longt", longitude);
        startActivity(intent);

    }
*/


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