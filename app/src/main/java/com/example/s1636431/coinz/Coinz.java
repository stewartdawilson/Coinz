package com.example.s1636431.coinz;

import android.app.Activity;

class Coinz {


    public Double getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public Activity getActivity() {

        return activity;
    }

    private Double value;
    private Activity activity;
    private String name;


    public Coinz(String name, Double value, Activity activity) {
        this.name = name;
        this.value = value;
        this.activity =  activity;
    }

}
