package com.sebsish.smarthomeapp.dataFormats;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    private String username;
    private String email;
    private String password;
    private Map<String, Object> modules;
    private ArrayList<Object> scenarios;

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getUsername() { return username; }
    public Map<String, Object> getModules() { return modules; }
    public ArrayList<Object> getScenarios() { return scenarios; }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.modules = new HashMap<String, Object>();
        this.scenarios = new ArrayList<Object>();
    }

    public User(String username, String email, String password, Map<String, Object> modules, ArrayList<Object> scenarios) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.modules = modules;
        this.scenarios = scenarios;
    }

}
