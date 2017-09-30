package com.schmidthappens.markd.data_objects;

import android.util.Log;

import org.json.JSONArray;
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
            contractorJson.put("contractorDetails", initialContractorDetails());
            contractorJson.put("customers", initialCustomerList());
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        contractor = new Contractor(contractorJson);
    }

    //Mark:- inital methods to remove when http calls are implemented
    private JSONObject initialContractorDetails() {
        JSONObject contractorJson = new JSONObject();
        try {
            contractorJson.put("companyName", "Greenwich Landscaping Company");
            contractorJson.put("telephoneNumber", "2038691022");
            contractorJson.put("websiteUrl", "http://greenwichlandscape.net/");
            contractorJson.put("zipCode", "53532");
            contractorJson.put("type", "landscaper");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return contractorJson;
    }
    private JSONArray initialCustomerList() {
        JSONArray customerArray = new JSONArray();
        try {
            JSONObject customer1 = new JSONObject();
            customer1.put("namePrefix", "Mr.");
            customer1.put("firstName", "Joshua");
            customer1.put("lastName", "Schmidt");
            customer1.put("nameSuffix", "");
            customer1.put("maritalStatus", "MARRIED");
            customerArray.put(customer1);

            JSONObject customer2 = new JSONObject();
            customer2.put("firstName", "Tiger");
            customer2.put("lastName", "Woods");
            customer2.put("maritalStatus", "single");
            customerArray.put(customer2);

            JSONObject customer3 = new JSONObject();
            customer3.put("firstName", "Bobby");
            customer3.put("lastName", "Burt");
            customer3.put("maritalStatus", "single");
            customerArray.put(customer3);

            JSONObject customer4 = new JSONObject();
            customer4.put("firstName", "Dirk");
            customer4.put("lastName", "BoatStuff");
            customer4.put("maritalStatus", "MARRIED");
            customerArray.put(customer4);

        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
            return customerArray;
        }
        return customerArray;
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
