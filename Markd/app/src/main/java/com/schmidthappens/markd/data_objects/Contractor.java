package com.schmidthappens.markd.data_objects;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/30/17.
 */

@IgnoreExtraProperties
public class Contractor {
    public final String userType= "contractor";
    private String namePrefix;
    private String firstName;
    private String lastName;
    private String type;
    private ContractorDetails contractorDetails;
    private List<Customer> customers;

    //Mark:- Constructors
    private Contractor(ContractorDetails details, List<Customer> customers) {
        this.contractorDetails = details;
        this.customers = customers;
    }
    public Contractor() {
        // Default constructor required for calls to DataSnapshot.getValue(Home.class)
    }

    //Mark:- Getters/Setters
    public String getNamePrefix() {
        return namePrefix;
    }
    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public ContractorDetails getContractorDetails() {
        return contractorDetails;
    }
    public void setContractorDetails(ContractorDetails contractorDetails) {
        this.contractorDetails = contractorDetails;
    }

    public List<Customer> getCustomers() {
        return customers;
    }
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    //Mark:- Helper functions
    public void updateProfile(String namePrefix, String firstName, String lastName, String contractorType) {
        this.namePrefix = namePrefix;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = contractorType;
    }
}
