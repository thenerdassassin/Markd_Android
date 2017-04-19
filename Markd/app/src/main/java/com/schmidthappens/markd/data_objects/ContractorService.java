package com.schmidthappens.markd.data_objects;

import android.support.annotation.NonNull;

/**
 * Created by Josh on 4/19/2017.
 */

public class ContractorService implements Comparable<ContractorService>{
    private int month;
    private int day;
    private int year;
    private String contractor;
    private String comments;
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

    // Mark:- Helper methods
    public String getDate() {
        return month + "/" + day + "/" + year;
    }

    @Override
    public int compareTo(@NonNull ContractorService o) {
        if(o == null) {
          return -1;
        }
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
