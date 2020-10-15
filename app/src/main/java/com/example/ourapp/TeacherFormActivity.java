package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class TeacherFormActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView imageToUpload;
    Uri selectedImage;
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


        }


    }
    public String getFileExtension(Uri uri)
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));

    }
    /*
    public void uploadImage(ImageView image,Uri uri){
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference ref= FirebaseStorage.getInstance().getReference().child("Images").child(userId+"."+getFileExtension(uri));
        ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){

                    @Override
                    public void onSuccess(Uri uri) {
                    String url=uri.toString();
                    Log.d("Downloadurl",url);
                    Toast.makeText(TeacherFormActivity.this,"Image upload successful",Toast.LENGTH_SHORT.show());
                    }
                }
            }
        })



    public  void submitForm(View view){

        FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference ref=database.getReference("Tutor");
        EditText name = findViewById(R.id.teacherNameET);
        EditText email = findViewById(R.id.teacherEmailET);
        EditText age = findViewById(R.id.teacherAge);
        EditText address = findViewById(R.id.teacherAddressET);
        EditText subject = findViewById(R.id.teacherSubjectsET);
        EditText salary = findViewById(R.id.teacherSalaryET);
        EditText experience = findViewById(R.id.teacherExperienceET);
        EditText phoneNumber = findViewById(R.id.teacherPhoneNumET);
        ImageView photo=findViewById(R.id.teacherPhotoImageView);



        String tName=name.getText().toString();
        String tEmail=email.getText().toString();
        String tAddress=address.getText().toString();
        int tAge=Integer.parseInt(age.getText().toString());
        float tSal=Float.parseFloat(salary.getText().toString());
        String tExp=experience.getText().toString();
        String tPhoneNum=phoneNumber.getText().toString();
        if(tName.isEmpty()){
            name.setError("Full name is required!");
            name.requestFocus();
            return;
        }
        if(tEmail.isEmpty()){
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }
        if(tAddress.isEmpty()){
            address.setError("Address is required!");
            address.requestFocus();
            return;
        }
        if(String.valueOf(tAge).isEmpty()){
            age.setError("Age is required!");
            age.requestFocus();
            return;
        }
        if(String.valueOf(tSal).isEmpty()){
            salary.setError("Salary is required!");
            salary.requestFocus();
            return;
        }
        if(tPhoneNum.isEmpty()){
            phoneNumber.setError("Phone number is required!");
            phoneNumber.requestFocus();
            return;
        }
        if(tExp.isEmpty()){
            experience.setError("Experience is required!");
            experience.requestFocus();
            return;
        }


        final TutorClass tutor=new TutorClass(tName,tEmail,tAddress,tExp,0.0,0.0,tAge);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(tutor);
            Toast.makeText(TeacherFormActivity.this,"You were successfully registered!",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    uploadImage(imageToUpload,selectedImage);
    }*/





    }




