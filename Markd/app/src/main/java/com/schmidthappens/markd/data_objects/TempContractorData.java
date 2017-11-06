package com.schmidthappens.markd.data_objects;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.utilities.OnGetDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/30/17.
 */

public class TempContractorData {
    private static final String TAG = "FirebaseContractorData";
    private static DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users");
    private String uid;
    private OnGetDataListener listener;

    public TempContractorData(FirebaseAuthentication authentication, OnGetDataListener listener) {
        this(authentication.getCurrentUser().getUid(), listener);
    }
    public TempContractorData(Activity activity, OnGetDataListener listener) {
        this(new FirebaseAuthentication(activity).getCurrentUser().getUid(), listener);
    }
    public TempContractorData(String uid, OnGetDataListener listener) {
        this.uid = uid;
        this.listener = listener;
        DatabaseReference userReference = database.child(uid);
        if(listener != null) {
            listener.onStart();
        }
        userReference.addValueEventListener(valueEventListener);
    }
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            contractor = dataSnapshot.getValue(Contractor.class);
            if(listener != null) {
                listener.onSuccess(dataSnapshot);
            }
            Log.d(TAG, "valueEventListener:dataChanged");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "valueEventListener:onCancelled", databaseError.toException());
            listener.onFailed(databaseError);
        }
    };

    private Contractor contractor;
    private Contractor getContractor() {
        return contractor;
    }
    private void putContractor(Contractor contractor) {
        Log.d(TAG, "putting contractor");
        database.child(uid).setValue(contractor);
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
    public void updateContractorDetails(ContractorDetails contractorDetails) {
        contractor.setContractorDetails(contractorDetails);
        putContractor(contractor);
    }

    //Mark:- Customers Page
    public List<Customer> getCustomers() {
        return getContractor().getCustomers();
    }

    //Mark:- Settings Page
    public void updateProfile(String namePrefix, String firstName, String lastName, String contractorType) {
        if(contractor == null) {
            Log.d(TAG, "customer null");
            contractor = new Contractor();
        }
        contractor.updateProfile(namePrefix, firstName, lastName, contractorType);
        putContractor(contractor);
    }
}
