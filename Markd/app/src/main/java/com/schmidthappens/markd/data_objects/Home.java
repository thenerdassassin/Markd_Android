package com.schmidthappens.markd.data_objects;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

@IgnoreExtraProperties
public class Home {
    private Double bedrooms;
    private Double squareFootage;
    private Double bathrooms;

    public Home() {
        // Default constructor required for calls to DataSnapshot.getValue(Home.class)
    }

    public Home(Double bedrooms, Double squareFootage, Double bathrooms) {
        this.bedrooms = bedrooms;
        this.squareFootage = squareFootage;
        this.bathrooms = bathrooms;
    }

    public Double getBedrooms() {
        return bedrooms;
    }
    public void setBedrooms(Double bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Double getSquareFootage() {
        return squareFootage;
    }
    public void setSquareFootage(Double squareFootage) {
        this.squareFootage = squareFootage;
    }

    public Double getBathrooms() {
        return bathrooms;
    }
    public void setBathrooms(Double bathrooms) {
        this.bathrooms = bathrooms;
    }
}
