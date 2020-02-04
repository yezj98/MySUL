package com.example.mysul.Model;

import java.util.HashMap;

public class User {
    private String uid, email;
    private HashMap<String, User> acceptlist;

    public User() {
    }

    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
        acceptlist = new HashMap<>();
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAcceptlist(HashMap<String, User> acceptlist) {
        this.acceptlist = acceptlist;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, User> getAcceptlist() {
        return acceptlist;
    }
}
