package com.example.mysul.Model;

public class post {
    private String phoneNumber, SecondphoneNumber, ThridphoneNumber;

    public post() {
    }

    public post(String phoneNumber, String secondphoneNumber, String thridphoneNumber) {
        this.phoneNumber = phoneNumber;
        SecondphoneNumber = secondphoneNumber;
        ThridphoneNumber = thridphoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSecondphoneNumber() {
        return SecondphoneNumber;
    }

    public void setSecondphoneNumber(String secondphoneNumber) {
        SecondphoneNumber = secondphoneNumber;
    }

    public String getThridphoneNumber() {
        return ThridphoneNumber;
    }

    public void setThridphoneNumber(String thridphoneNumber) {
        ThridphoneNumber = thridphoneNumber;
    }
}
