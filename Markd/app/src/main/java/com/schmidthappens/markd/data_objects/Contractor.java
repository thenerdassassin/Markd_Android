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
}
