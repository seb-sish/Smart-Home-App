package com.sebsish.smarthomeapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String password;
    public HashMap<String, String> devices;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.devices = new HashMap<>();
    }

    public User(String username, String email, String password, HashMap<String, String> devices) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.devices = devices;
    }

}
