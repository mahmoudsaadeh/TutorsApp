package com.example.ourapp;

import android.widget.TextView;

public class commonMethods {
    public static void Warning(TextView textview,String warning) {

        textview.setError(warning);
        textview.requestFocus();
    }

}
