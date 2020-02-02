package com.markd.applications.android.home.data_objects;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Josh Schmidt on 9/23/17.
 */

@IgnoreExtraProperties
public class Home {
    private Double bedrooms;
    private Integer squareFootage;
    private Double bathrooms;

    public Home() {
        // Default constructor required for calls to DataSnapshot.getValue(Home.class)
    }

    public Home(Double bedrooms, Double bathrooms, Integer squareFootage) {
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.squareFootage = squareFootage;
    }

    public Double getBedrooms() {
        return bedrooms;
    }
    public void setBedrooms(Double bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Double getBathrooms() {
        return bathrooms;
    }
    public void setBathrooms(Double bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Integer getSquareFootage() {
        return squareFootage;
    }
    public void setSquareFootage(Integer squareFootage) {
        this.squareFootage = squareFootage;
    }

}
