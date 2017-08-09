package com.schmidthappens.markd.data_objects;

import android.support.annotation.NonNull;

import com.schmidthappens.markd.utilities.StringUtilities;

import java.util.List;

/**
 * Created by Josh on 4/19/2017.
 */

public class ContractorService implements Comparable<ContractorService>{
    private int month;
    private int day;
    private int year;
    private String contractor;
    private String comments;
    private List<String> imageFiles;
    //TODO add image uploads


    public ContractorService(int month, int day, int year, String contractor, String comments) {
        this.month = month;
        this.day = day;
        this.year = year;
        this.contractor = contractor;
        this.comments = comments;
    }
    // Mark:- Getters/Setters
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
    public String getContractor() {
        return contractor;
    }
    public void setContractor(String contractor) {
        this.contractor = contractor;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public List<String> getImageFiles() {
        return imageFiles;
    }
    public void setImageFiles(List<String> imageFiles) {
        this.imageFiles = imageFiles;
    }

    public void update(String contractor, String comments) {
        this.contractor = contractor;
        this.comments = comments;
    }

    // Mark:- Helper methods
    public String getDate() {
        return StringUtilities.getDateString(month, day, year);
    }

    @Override
    public int compareTo(@NonNull ContractorService o) {
        if(this == o) {
            return 0;
        }

        if(this.year != o.year) {
            return o.year - this.year;
        }

        if(this.month != o.month) {
            return o.month - this.month;
        }

        return o.day - this.day;
    }
}
