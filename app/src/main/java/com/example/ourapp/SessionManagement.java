package com.example.ourapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    int ID;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME="session";
    String SESSION_KEY="session_user";

    public SessionManagement(Context context){
        sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public void saveSession(int id){
        ID=id;
        editor.putInt(SESSION_KEY,id).commit();
    }

    public int getSession(){
        return sharedPreferences.getInt(SESSION_KEY,ID);
    }

    public void removeSession(){
        editor.putInt(SESSION_KEY,-1).commit();
    }
}
