package com.example.ourapp;


public class TutorClass {
    public String name,email,address,experience, subject, phoneNum, age, salary, longitude,latitude, location, imageUrl;

    public TutorClass(){}

    public TutorClass(String tName, String tEmail, String tAddress, String tExperience, String tLat, String tLong, String location, String tAge, String subject, String phone, String salary, String imageUrl){
        this.name=tName;
        this.email=tEmail;
        this.address=tAddress;
        this.experience=tExperience;
        this.longitude=tLong;
        this.latitude=tLat;
        this.age=tAge;
        this.subject = subject;
        this.phoneNum = phone;
        this.salary = salary;
        this.location = location;
        this.imageUrl = imageUrl;
    }
}

