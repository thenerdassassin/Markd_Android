package com.schmidthappens.markd.data_objects;

/**
 * Created by Josh on 5/29/2017.
 */


public class PaintObject {
    private String location;
    private String manufacturer;
    private String color;

    public PaintObject(String location, String manufacturer, String color) {
        this.location = location;
        this.manufacturer = manufacturer;
        this.color = color;
    }

    public String getLocation() {
        return location;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getColor() {
        return color;
    }
}
