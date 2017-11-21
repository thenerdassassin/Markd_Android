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
    public Contractor setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }
    public Contractor setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }
    public Contractor setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getType() {
        return type;
    }
    public Contractor setType(String type) {
        this.type = type;
        return this;
    }

    public ContractorDetails getContractorDetails() {
        return contractorDetails;
    }
    public Contractor setContractorDetails(ContractorDetails contractorDetails) {
        this.contractorDetails = contractorDetails;
        return this;
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
