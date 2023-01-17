package com.example.pj_projekt.data;

import java.util.ArrayList;

class MatrixCall {
    private ArrayList<ArrayList<Double>> locations;
    private String metrics;
    private final String units = "km";

    public MatrixCall(String metrics, ArrayList<ArrayList<Double>>locations) {
        this.metrics = metrics;
        this.locations = locations;
    }
}
