package com.schmidthappens.markd.data_objects;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/30/17.
 */

public class TempContractorData {
    private static final String TAG = "ContractorDataSingleton";
    private static final TempContractorData contractorData = new TempContractorData();
    public static TempContractorData getInstance() {
        return contractorData;
    }

    private Contractor contractor;

    private Contractor getContractor() {
        //TODO: Add get http call to ensure contractor is up to date
        //Get user id and sessionToken from SessionManager
        //HTTP call receive back body data
        //Check for success in body data
        //Get message as string
        //new JSONObject(message)
        //contractor = Contractor(new JSONObject(message))
        return contractor;
    }
    private boolean putContractor(Contractor contractor) {
        //TODO: Add http put call to set in database
        return true;
    }

    //Update Contractor
    private void updatedContractor(ContractorDetails details) {
        contractor.setContractorDetails(details);
    }


    private TempContractorData() {
        contractor = getContractor();
        //Remove when database is implemented
        JSONObject contractorJson = new JSONObject();
        try {
            contractorJson.put("companyName", "Greenwich Landscaping Company");
            contractorJson.put("telephoneNumber", "2038691022");
            contractorJson.put("websiteUrl", "http://greenwichlandscape.net/");
            contractorJson.put("zipCode", "53532");
            contractorJson.put("type", "landscaper");

            contractorJson.put("customers", initialCustomerList());
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        contractor = new Contractor(contractorJson);
    }

    //Mark:- inital methods to remove when http calls are implemented
    private List<Customer> initialCustomerList() {
        return null;
    }

    //Mark:- Home Page
    public ContractorDetails getContractorDetails() {
        return getContractor().getContractorDetails();
    }
    public boolean updateContractorDetails(ContractorDetails contractorDetails) {
        Contractor oldContractor = getContractor();                       //update contractor
        Contractor contractorToUpdate = new Contractor(oldContractor);   //make copy
        contractorToUpdate.setContractorDetails(contractorDetails);     //change component on copy
        if(putContractor(contractorToUpdate)) {                        //send to database
            this.updatedContractor(contractorDetails);                //update TempContractorData
            return true;
        }
        return false;
    }

    //Mark:- Customers Page
    public List<Customer> getCustomers() {
        return getContractor().getCustomers();
    }
}
