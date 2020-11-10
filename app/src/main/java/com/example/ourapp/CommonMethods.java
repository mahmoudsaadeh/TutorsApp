package com.example.ourapp;

import android.widget.TextView;

public class CommonMethods {
    public static void Warning(TextView textview,String warning) {

        textview.setError(warning);
        textview.requestFocus();
    }

}
