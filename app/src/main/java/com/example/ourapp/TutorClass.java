package com.example.ourapp;

import com.google.firebase.database.FirebaseDatabase;

public class TutorClass {
    String name,email,address,experience;
    double longitude,latitude;
    int age;
    public TutorClass(){}
    public TutorClass(String tName,String tEmail,String tAddress,String tExperience,double tLong,double tLat,int tAge){
        this.name=tName;
        this.email=tEmail;
        this.address=tAddress;
        this.experience=tExperience;
        this.longitude=tLong;
        this.latitude=tLat;
        this.age=tAge;



    }
}

