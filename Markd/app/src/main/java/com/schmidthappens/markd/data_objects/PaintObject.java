package com.schmidthappens.markd.data_objects;

/**
 * Created by Josh on 5/29/2017.
 */


public class PaintObject {
    private String location;
    private String brand;
    private String color;

    public PaintObject(String location, String brand, String color) {
        this.location = location;
        this.brand = brand;
        this.color = color;
    }

    public String getLocation() {
        return location;
    }

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }
}
