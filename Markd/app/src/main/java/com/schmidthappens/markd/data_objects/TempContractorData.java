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
        //contractor = new Contractor(contractorJson);
    }

    //Mark:- initial methods to remove when http calls are implemented
    private JSONObject initialContractorDetails() {
        JSONObject contractor = new JSONObject();
        try {
            contractor.put("companyName", "SDR Plumbing & Heating Inc");
            contractor.put("telephoneNumber", "203.348.2295");
            contractor.put("websiteUrl", "sdrplumbing.com");
            contractor.put("profession", "plumber");
            contractor.put("zipCode", "06903");
        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return contractor;
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
            customer1.put("address", (new JSONObject()
                    .put("street", "1571 Day Street")
                    .put("state", "WI")
                    .put("zipCode", "54126")
                    .put("city", "Greenleaf")));
            customerArray.put(customer1);

            JSONObject customer2 = new JSONObject();
            customer2.put("firstName", "Tiger");
            customer2.put("lastName", "Woods");
            customer2.put("maritalStatus", "single");
            customer2.put("address", (new JSONObject()
                    .put("street", "135 Maple Tree Hill Rd")
                    .put("state", "CT")
                    .put("zipCode", "06214")
                    .put("city", "Oxford")));
            customerArray.put(customer2);

            JSONObject customer3 = new JSONObject();
            customer3.put("firstName", "Bobby");
            customer3.put("lastName", "Burt");
            customer3.put("maritalStatus", "single");
            customer3.put("address", (new JSONObject()
                    .put("street", "800 Discovery Way Apt 824")
                    .put("state", "NC")
                    .put("zipCode", "27703")
                    .put("city", "Durham")));
            customerArray.put(customer3);

            JSONObject customer4 = new JSONObject();
            customer4.put("firstName", "Dirk");
            customer4.put("lastName", "Smithsonian");
            customer4.put("maritalStatus", "MARRIED");
            customer4.put("address", (new JSONObject()
                    .put("street", "1234 Mainish Street")
                    .put("state", "NY")
                    .put("zipCode", "34567")
                    .put("city", "Buffalo")));
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
//        Contractor oldContractor = getContractor();                       //update contractor
//        Contractor contractorToUpdate = new Contractor(oldContractor);   //make copy
//        contractorToUpdate.setContractorDetails(contractorDetails);     //change component on copy
//        if(putContractor(contractorToUpdate)) {                        //send to database
//            this.updatedContractor(contractorDetails);                //update TempContractorData
//            return true;
//        }
        return false;
    }

    //Mark:- Customers Page
    public List<Customer> getCustomers() {
        return getContractor().getCustomers();
    }
}
