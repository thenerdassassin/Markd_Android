package com.schmidthappens.markd.data_objects;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.StorageException;
import com.schmidthappens.markd.utilities.DateUtitilities;
import com.schmidthappens.markd.utilities.StringUtilities;

import java.util.List;
import java.util.UUID;

/**
 * Created by Josh on 4/19/2017.
 */

@IgnoreExtraProperties
public class ContractorService implements Comparable<ContractorService> {
    private String guid;
    private int month;
    private int day;
    private int year;
    private String contractor;
    private String comments;
    private List<String> files;


    public ContractorService() {
        // Default constructor required for calls to DataSnapshot.getValue(ContractorService.class)
    }
    public ContractorService(int month, int day, int year, String contractor, String comments, List<String> files) {
        this.month = month;
        this.day = day;
        this.year = year;
        this.contractor = contractor;
        this.comments = comments;
        this.files = files;
        setGuid(null);
    }
    public ContractorService(String contractor, String comments, List<String> files) {
        this.month = DateUtitilities.getCurrentMonth();
        this.day = DateUtitilities.getCurrentDay();
        this.year = DateUtitilities.getCurrentYear();
        this.contractor = contractor;
        this.comments = comments;
        this.files = files;
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
    public List<String> getFiles() {
        return files;
    }
    public void setFiles(List<String> files) {
        this.files = files;
    }
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        if(guid == null) {
            this.guid = UUID.randomUUID().toString();
        } else {
            this.guid = guid;
        }
    }

    @Exclude
    public String getFilePath(String customerId, int fileId) {
        if(StringUtilities.isNullOrEmpty(customerId) || StringUtilities.isNullOrEmpty(guid) || fileId < 0 || fileId >= files.size()) {
            return "";
        }
        return "services/"+customerId+"/"+guid+"/"+files.get(fileId);
    }

    public ContractorService update(String contractor, String comments, List<String> files) {
        this.contractor = contractor;
        this.comments = comments;
        this.files = files;
        return this;
    }

    // Mark:- Helper methods
    @Exclude
    public String getDate() {
        return StringUtilities.getDateString(month, day, year);
    }
    @Exclude
    public String getUrlFormattedString() {
        return StringUtilities.getDateString(month, day, year, "");
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
    @Override
    public String toString() {
        return " {\n" +
                "\tdate:\"" + getDate() + "\",\n" +
                "\tcontractor:\"" + getContractor() + "\",\n" +
                "\tcomments:\"" + getComments() + "\",\n" +
                "\timages:" + getFiles() + "\n" +
                "}";
    }
}
