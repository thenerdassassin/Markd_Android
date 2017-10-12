package com.schmidthappens.markd.data_objects;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.schmidthappens.markd.utilities.StringUtilities;

/**
 * Created by Josh on 5/29/2017.
 */

@IgnoreExtraProperties
public class PaintSurface {
    private String location;
    private String brand;
    private String color;
    private int month;
    private int day;
    private int year;

    public PaintSurface(String location, String brand, String color, int month, int day, int year) {
        this.location = location;
        this.brand = brand;
        this.color = color;
        this.month = month;
        this.day = day;
        this.year = year;
    }
    public PaintSurface() {
        // Default constructor required for calls to DataSnapshot.getValue(PaintSurface.class)
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public int getMonth() {
        return month;
    }
    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }
    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    /*
        Returns string in format mm.dd.yy
        If something is wrong returns null
     */
    @Nullable @Exclude
    public String getDateString() {
        return StringUtilities.getDateString(month, day, year);
    }
}
