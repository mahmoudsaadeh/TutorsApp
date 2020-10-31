package com.example.ourapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
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
    int flag;
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

    StorageReference storageReference;
    DatabaseReference databaseReference;

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

    String imageUrl = "";

    String username;


    double progressPercentage;
    //deleted TeacherEditInfoForm Activity
    //will use if else statements to change TextView's texts in OnCreate



    public void openGallery(View view) {
        //link to try to get images from gallery for api > 24 phones
        //https://stackoverflow.com/questions/2169649/get-pick-an-image-from-androids-built-in-gallery-app-programmatically

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }




    private boolean checkForTableExists(SQLiteDatabase db, String table){
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.getCount() > 0) {
            return true;
        }
        mCursor.close();
        return false;
    }




    public void goToMap(View view){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

        try {

            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("TutorData", MODE_PRIVATE, null);

            boolean checkTableExist = checkForTableExists(sqLiteDatabase, "tutorData");

            Log.d("gotomap","1");

            if(checkTableExist){
                //sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tutorData (id VARCHAR, name VARCHAR, mail VARCHAR, age VARCHAR, address VARCHAR, subjects VARCHAR, salary VARCHAR, experience VARCHAR, phone VARCHAR, imgURI VARCHAR, location VARCHAR, longitude VARCHAR, latitude VARCHAR)");

                Log.d("gotomap","1.5");

                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM tutorData WHERE id = '" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "' LIMIT 1", null);
                if (cursor.getCount() > 0) {

                    Log.d("gotomapp", "2");
                    sqLiteDatabase.execSQL("UPDATE tutorData SET name = '" + name.getText().toString() + "', mail = '" +
                            email.getText().toString() + "', age = '" + age.getText().toString() + "', address = '" +
                            address.getText().toString() + "', subjects = '" + subject.getText().toString() + "', " +
                            "salary = '" + salary.getText().toString() + "', experience = '" + experience.getText().toString() + "', " +
                            "phone = '" + phoneNumber.getText().toString() + "', imgURI = '" + selectedImage + "'");
                }
                else{
                    Log.d("gotomappz", "3");
                    sqLiteDatabase.execSQL("INSERT INTO tutorData (id, name,mail,age,address,subjects,salary,experience,phone,imgURI) " +
                            "VALUES ('" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "', '" + name.getText().toString() + "', '" +
                            email.getText().toString() + "', '" + age.getText().toString() + "', '" +
                            address.getText().toString() + "', '" + subject.getText().toString() + "', '" + salary.getText().toString() + "', '" +
                            experience.getText().toString() + "', '" + phoneNumber.getText().toString() + "', '" + selectedImage + "')");

                }
            }
            else {
                Log.d("gotomap","3");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS tutorData (id VARCHAR, name VARCHAR, mail VARCHAR, age VARCHAR, address VARCHAR, subjects VARCHAR, salary VARCHAR, experience VARCHAR, phone VARCHAR, imgURI VARCHAR, location VARCHAR, longitude VARCHAR, latitude VARCHAR)");

                sqLiteDatabase.execSQL("INSERT INTO tutorData (id, name,mail,age,address,subjects,salary,experience,phone,imgURI) " +
                        "VALUES ('" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "', '" + name.getText().toString() + "', '" +
                        email.getText().toString() + "', '" + age.getText().toString() + "', '" +
                        address.getText().toString() + "', '" + subject.getText().toString() + "', '" + salary.getText().toString() + "', '" +
                        experience.getText().toString() + "', '" + phoneNumber.getText().toString() + "', '" + selectedImage + "')");


            }
            sqLiteDatabase.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        startActivity(intent);
        finish();
    }









    @Override
    protected void onCreate(Bundle savedInstanceState) {


       try{
           flag=Integer.parseInt(getIntent().getStringExtra("FLAG"));
       }
       catch(Exception e){
           e.printStackTrace();
       }

        if(flag!=1) {

            DatabaseReference stRef2=FirebaseDatabase.getInstance().getReference().child("TutorFormInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            stRef2.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        autoFill();

                    } else {
                        // Don't exist! Do something.

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_form);

        Intent i = getIntent();
        username = i.getStringExtra("username");

        setTitle("" + username);

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

        /*Intent intent = getIntent();
        lat = intent.getStringExtra("Latitude");
        lon = intent.getStringExtra("Longitude");
        addressLine = intent.getStringExtra("AddressLine");

        if(addressLine != null || lat != null || lon != null){
            chosenLocation.setText(addressLine);
        }*/

        try {
            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("TutorData", MODE_PRIVATE, null);

            if(checkForTableExists(sqLiteDatabase, "tutorData")){
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM tutorData WHERE id = '" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "' LIMIT 1", null);

                Log.d("cursor1", "acccessed");
                Log.d("getcount", String.valueOf(cursor.getCount()));

                if (cursor.getCount() > 0) {

                    Log.d("cursor2", "acccessed");

                    int idIndex = cursor.getColumnIndex("id");
                    int nameIndex = cursor.getColumnIndex("name");
                    int mailIndex = cursor.getColumnIndex("mail");
                    int ageIndex = cursor.getColumnIndex("age");
                    int addressIndex = cursor.getColumnIndex("address");
                    int subjectsIndex = cursor.getColumnIndex("subjects");
                    int salaryIndex = cursor.getColumnIndex("salary");
                    int experienceIndex = cursor.getColumnIndex("experience");
                    int phoneIndex = cursor.getColumnIndex("phone");
                    int imgUriIndex = cursor.getColumnIndex("imgURI");
                    int locationIndex = cursor.getColumnIndex("location");
                    int lonIndex = cursor.getColumnIndex("longitude");
                    int latIndex = cursor.getColumnIndex("latitude");

                    cursor.moveToFirst();

                    Log.i("id", cursor.getString(idIndex));
                    Log.i("name", cursor.getString(nameIndex));
                    Log.i("mail", cursor.getString(mailIndex));
                    Log.i("age", cursor.getString(ageIndex));
                    Log.i("address", cursor.getString(addressIndex));
                    Log.i("subjects", cursor.getString(subjectsIndex));
                    Log.i("salary", cursor.getString(salaryIndex));
                    Log.i("experience", cursor.getString(experienceIndex));
                    Log.i("phone", cursor.getString(phoneIndex));
                    Log.i("imgUri", cursor.getString(imgUriIndex));
                    Log.i("location", cursor.getString(locationIndex));
                    Log.i("lon", cursor.getString(lonIndex));
                    Log.i("lat", cursor.getString(latIndex));

                    name.setText(cursor.getString(nameIndex));
                    email.setText(cursor.getString(mailIndex));
                    age.setText(cursor.getString(ageIndex));
                    address.setText(cursor.getString(addressIndex));
                    subject.setText(cursor.getString(subjectsIndex));
                    salary.setText(cursor.getString(salaryIndex));
                    experience.setText(cursor.getString(experienceIndex));
                    phoneNumber.setText(cursor.getString(phoneIndex));
                    chosenLocation.setText(cursor.getString(locationIndex));

                    selectedImage=Uri.parse(cursor.getString(imgUriIndex));

                    //if(lat.equals("") && lon.equals("")) {
                    lon = cursor.getString(lonIndex);
                    lat = cursor.getString(latIndex);
                    addressLine = cursor.getString(locationIndex);
                    //}
            /*if(cursor.getString(imgUriIndex) != null){
                selectedImage = Uri.parse(cursor.getString(imgUriIndex));
                imageToUpload.setImageURI(selectedImage);
            }*/


                    FirebaseStorage storage=FirebaseStorage.getInstance();
                    final StorageReference stRef=storage.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profilePhoto.jpg");
                    imageUrl=selectedImage.toString();
                    stRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>()
                    {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bm= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            imageToUpload.setImageBitmap(bm);


                        }



                    });

                    cursor.close();
                }
            }
            sqLiteDatabase.close();







        }
        catch (Exception e){
            e.printStackTrace();
        }

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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("checkuser","" + user.getEmail());
                FirebaseAuth.getInstance().signOut();

                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Log.d("signout","successful");
                    SessionManagement sessionManagement=new SessionManagement(TeacherFormActivity.this);
                    sessionManagement.removeSession();
                    Intent intent3 = new Intent(getApplicationContext(), MainActivityLogin.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    try {
                        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("TutorData", MODE_PRIVATE, null);
                        sqLiteDatabase.execSQL("DELETE FROM tutorData");
                        sqLiteDatabase.close();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

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
        storageReference = FirebaseStorage.getInstance().getReference().child(userId).child("profilePhoto"+"."+getFileExtension(uri));

        final ProgressDialog pd = new ProgressDialog(TeacherFormActivity.this);
        pd.setTitle("Uploading image...");
        pd.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(TeacherFormActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();

                Log.i("test1","reached");
                if (taskSnapshot.getMetadata() != null) {
                    Log.i("test2","reached");
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Log.i("test3","reached");
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl = uri.toString();
                                Log.i("test4","reached");
                                Log.i("imgurl","" + imageUrl);
                            }
                        });

                    }
                }

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
                progressPercentage = (100.0 * (snapshot.getBytesTransferred() * 1.0 / snapshot.getTotalByteCount()));
                pd.setMessage("Progress: " + (int) progressPercentage + "%");
            }
        });

    }






    public  void submitForm (View view){

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

        if(imageToUpload.getDrawable()==null){
            Toast.makeText(this, "You should select an profile photo before you submit!", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if(addressLine == null || lat == null || lon == null){
                Toast.makeText(this, "Location required", Toast.LENGTH_SHORT).show();
                chosenLocation.setError("Location is required!");
                chosenLocation.requestFocus();
                return;
            }
           else {
               // uploadImage(selectedImage);
            }
        }

        Log.d("latz", "submitForm: " + lat);
        Log.d("lonz", "submitForm: " + lon);


        /*while (imageUrl.isEmpty()){
            Log.i("waiting for image to upload","waiting");
        }*/

        final TutorClass tutor = new TutorClass(tName, tEmail, tAddress, tExp, lat, lon, addressLine, tAge, subj, tPhoneNum, tSal, imageUrl);

        Log.i("imgURL2", "" + imageUrl);

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


    private void autoFill() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("TutorFormInfo");
        final String id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child(id).child("name").getValue(String.class));
                email.setText(snapshot.child(id).child("email").getValue(String.class));
                age.setText(snapshot.child(id).child("age").getValue(String.class));
                address.setText(snapshot.child(id).child("address").getValue(String.class));
                subject.setText(snapshot.child(id).child("subject").getValue(String.class));
                salary.setText(snapshot.child(id).child("salary").getValue(String.class));
                experience.setText(snapshot.child(id).child("experience").getValue(String.class));
                phoneNumber.setText(snapshot.child(id).child("phoneNum").getValue(String.class));
                chosenLocation.setText(snapshot.child(id).child("location").getValue(String.class));
                imageUrl=snapshot.child(id).child("imageUrl").getValue(String.class);
                lon = snapshot.child(id).child("longitude").getValue(String.class);
                lat = snapshot.child(id).child("latitude").getValue(String.class);
                addressLine=snapshot.child(id).child("location").getValue(String.class);
                FirebaseStorage storage=FirebaseStorage.getInstance();
                final StorageReference stRef=storage.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profilePhoto.jpg");
                selectedImage=Uri.parse(imageUrl);
                stRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>()
                {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imageToUpload.setImageBitmap(bm);


                    }



                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




}//end class







