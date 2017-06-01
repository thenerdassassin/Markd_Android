package com.schmidthappens.markd.data_objects;

/**
 * Created by Josh on 6/1/2017.
 */

public class LandscapingSeason {
    private String season;
    private int year;
    private double poundsPerAcre;
    private int rating;
    private String comments;

    public LandscapingSeason(String season, int year, double poundsPerAcre, int rating, String comments) {
        this.season = season;
        this.year = year;
        this.poundsPerAcre = poundsPerAcre;
        this.rating = rating;
        this.comments = comments;
    }

    public String getSeason() {
        return season;
    }

    public int getYear() {
        return year;
    }

    public double getPoundsPerAcre() {
        return poundsPerAcre;
    }

    public int getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }
}
