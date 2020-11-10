package com.example.ourapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CommonMethods {

    public static final int PASSWORD_LENGTH = 6;
    public static final int STUDENT_ID = 1;
    public static final int TUTOR_ID = 2;

    public static void warning(TextView textview, String warning) {
        textview.setError(warning);
        textview.requestFocus();
    }


    // common methods in all activities

    public static void displayLoadingScreen(ProgressDialog progressDialog) {
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    public static void makeToast(Context context, String toastMessage) {
        Toast.makeText(context, "" + toastMessage, Toast.LENGTH_SHORT).show();
    }

    public static boolean checkIfEmpty(String string){
        return string.isEmpty();
    }

    public static boolean checkForTableExists(SQLiteDatabase db, String table) {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";
        Cursor mCursor = db.rawQuery(sql, null);

        if (mCursor.getCount() > 0) {
            return true;
        }

        mCursor.close();

        return false;
    }


    //login methods (some are used in sign up as well)

    public static boolean isNotAnEmail(String email){
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean checkIfPassLengthNotValid(String password){
        return password.length() < PASSWORD_LENGTH;
    }


    public static String getEmail(EditText username) {
        return username.getText().toString().trim();
    }

    public static String getPassword(EditText password) {
        return password.getText().toString().trim();
    }


    public static boolean isStudent(int id) {
        return id == STUDENT_ID;
    }


    public static boolean isTutor(int id) {
        return id == TUTOR_ID;
    }



    //sign up methods

    public static boolean checkIfConfirmPassMatchesPass(String confPass, String pass){
        return confPass.equals(pass);
    }


    // teacher form methods



}
