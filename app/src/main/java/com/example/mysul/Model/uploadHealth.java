package com.example.mysul.Model;

public class uploadHealth {
    private String name, date, weight, height, medicine, illness, skin, blood;

    public uploadHealth() {

    }

    public uploadHealth(String name, String date, String weight, String height, String medicine, String illness, String skin, String blood) {
        this.name = name;
        this.date = date;
        this.weight = weight;
        this.height = height;
        this.medicine = medicine;
        this.illness = illness;
        this.skin = skin;
        this.blood = blood;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getIllness() {
        return illness;
    }

    public void setIllness(String illness) {
        this.illness = illness;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }
}