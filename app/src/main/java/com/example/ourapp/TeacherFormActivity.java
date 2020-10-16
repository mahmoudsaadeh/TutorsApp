package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class TeacherFormActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView imageToUpload;
    Uri selectedImage;

    EditText name;
    EditText email;
    EditText age;
    EditText address;
    EditText subject;
    EditText salary;
    EditText experience;
    EditText phoneNumber;

    TextView chosenLocation;

    StorageReference ref;

    String lat = "";
    String lon = "";
    String addressLine = "";

    String tName;
    String tEmail;
    String tAddress;
    String tAge;
    String tSal;
    String tExp;
    String tPhoneNum;
    String subj;


    public void openGallery(View view) {
        //link to try to get images from gallery for api > 24 phones
        //https://stackoverflow.com/questions/2169649/get-pick-an-image-from-androids-built-in-gallery-app-programmatically

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    public void goToMap(View view){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_form);

        setTitle("TutorApp - Tutor Info Form");

        imageToUpload = (ImageView) findViewById(R.id.teacherPhotoImageView);

        name = findViewById(R.id.teacherNameET);
        email = findViewById(R.id.teacherEmailET);
        age = findViewById(R.id.teacherAge);
        address = findViewById(R.id.teacherAddressET);
        subject = findViewById(R.id.teacherSubjectsET);
        salary = findViewById(R.id.teacherSalaryET);
        experience = findViewById(R.id.teacherExperienceET);
        phoneNumber = findViewById(R.id.teacherPhoneNumET);

        chosenLocation = findViewById(R.id.yourChosenLocation);

        Intent intent = getIntent();
        lat = intent.getStringExtra("Latitude");
        lon = intent.getStringExtra("Longitude");
        addressLine = intent.getStringExtra("AddressLine");

        if(addressLine != null || lat != null || lon != null){
            chosenLocation.setText(addressLine);
        }

        /*Log.d("lat2", "" + lat);
        Log.d("lon2", "" + lon);
        Log.d("addr2", "" + addressLine);*/
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
                    SessionManagement sessionManagement=new SessionManagement(TeacherFormActivity.this);
                    sessionManagement.removeSession();
                    Intent intent3 = new Intent(getApplicationContext(), MainActivityLogin.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent3);
                }else{
                    Log.d("signout","failed");
                    Toast.makeText(this, "Logout Failed!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return true;
        //return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);

            uploadImage(selectedImage);
        }


    }
    public String getFileExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));

    }
    

    public void uploadImage(Uri uri) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseStorage.getInstance().getReference().child(userId).child("profilePhoto"+"."+getFileExtension(uri));

        final ProgressDialog pd = new ProgressDialog(TeacherFormActivity.this);
        pd.setTitle("Uploading image...");
        pd.show();


        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(TeacherFormActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(TeacherFormActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Progress: " + (int) progressPercentage + "%");
            }
        });
/*
        ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Log.d("Downloadurl", url);
                        pd.dismiss();
                        //Toast.makeText(TeacherFormActivity.this, "Image upload successful!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Progress: " + (int) progressPercentage + "%");
            }
        });

 */
    }


    public  void submitForm (View view){

        //FirebaseDatabase database= FirebaseDatabase.getInstance();

        //final DatabaseReference databaseReference = database.getReference("Tutor");

        DatabaseReference databaseReference;

      /*  EditText name = findViewById(R.id.teacherNameET);
        EditText email = findViewById(R.id.teacherEmailET);
        EditText age = findViewById(R.id.teacherAge);
        EditText address = findViewById(R.id.teacherAddressET);
        EditText subject = findViewById(R.id.teacherSubjectsET);
        EditText salary = findViewById(R.id.teacherSalaryET);
        EditText experience = findViewById(R.id.teacherExperienceET);
        EditText phoneNumber = findViewById(R.id.teacherPhoneNumET);
        ImageView photo = findViewById(R.id.teacherPhotoImageView);*/

        tName = name.getText().toString();
        tEmail = email.getText().toString();
        tAddress = address.getText().toString();
        tAge = age.getText().toString();
        tSal = salary.getText().toString();
        tExp = experience.getText().toString();
        tPhoneNum = phoneNumber.getText().toString();
        subj = subject.getText().toString();


        if(tName.isEmpty()){
            name.setError("Full name is required!");
            name.requestFocus();
            return;
        }
        else{

        }

        if(tEmail.isEmpty()){
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(tEmail).matches()){
            email.setError("Please provide a correct email address!");
            email.requestFocus();
            return;
        }

        if(tAge.isEmpty()){
            age.setError("Age is required!");
            age.requestFocus();
            return;
        }

        if(tAddress.isEmpty()){
            address.setError("Address is required!");
            address.requestFocus();
            return;
        }

        if(subj.isEmpty()){
            subject.setError("Subject field is required!");
            subject.requestFocus();
            return;
        }

        if(tSal.isEmpty()){
            salary.setError("Salary is required!");
            salary.requestFocus();
            return;
        }

        if(tExp.isEmpty()){
            experience.setError("Experience is required!");
            experience.requestFocus();
            return;
        }

        if(tPhoneNum.isEmpty()){
            phoneNumber.setError("Phone number is required!");
            phoneNumber.requestFocus();
            return;
        }

        if(selectedImage == null){
            Toast.makeText(this, "You should select an profile photo before you submit!", Toast.LENGTH_SHORT).show();
            return;
        }
       /* else{
            uploadImage(selectedImage);
        }*/

        if(addressLine == null || lat == null || lon == null){
            Toast.makeText(this, "Location required", Toast.LENGTH_SHORT).show();
            chosenLocation.setError("Location is required!");
            chosenLocation.requestFocus();
            return;
        }


        final TutorClass tutor = new TutorClass(tName, tEmail, tAddress, tExp, lat, lon, tAge, subj, tPhoneNum, tSal, addressLine);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("TutorFormInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.setValue(tutor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(TeacherFormActivity.this, "Data inserted successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}//end class







