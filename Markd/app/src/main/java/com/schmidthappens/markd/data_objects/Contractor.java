package com.schmidthappens.markd.data_objects;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/30/17.
 */

public class Contractor {
    private static final String TAG = "ContractorBean";

    private ContractorDetails contractorDetails;
    private List<Customer> customers;

    private Contractor(ContractorDetails details, List<Customer> customers) {
        this.contractorDetails = details;
        this.customers = customers;
    }
    public Contractor(JSONObject contractorJSON) {
        super();
        if(contractorJSON.optJSONObject("contractorDetails") != null) {
            this.contractorDetails = new ContractorDetails(contractorJSON.optJSONObject("contractorDetails"));
        } else {
            Log.e(TAG, "No contractor details in JSONObject");
            this.contractorDetails = null;
        }
        this.customers = buildCustomerListFromJSONArray(contractorJSON.optJSONArray("customers"));
    }
    private List<Customer> buildCustomerListFromJSONArray(JSONArray customers) {
        List<Customer> customerList = new ArrayList<>();
        if(customers == null) {
            return customerList;
        }
        for(int i = 0; i < customers.length(); i++) {
            JSONObject customer = customers.optJSONObject(i);
            if(customer != null) {
                customerList.add(new Customer(customer));
            }
        }
        return customerList;
    }
    Contractor(Contractor contractor) {
        this(contractor.getContractorDetails(), contractor.getCustomers());
    }

    //Mark:- Getters/Setters
    ContractorDetails getContractorDetails() {
        return contractorDetails;
    }
    void setContractorDetails(ContractorDetails contractorDetails) {
        this.contractorDetails = contractorDetails;
    }
    List<Customer> getCustomers() {
        return customers;
    }
}
