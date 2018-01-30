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

import java.util.Collections;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/30/17.
 */

public class TempContractorData {
    private static final String TAG = "FirebaseContractorData";
    private static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private String uid;
    private DatabaseReference userReference;
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
        userReference = database.child("users").child(uid);
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
    public void removeListener() {
        userReference.removeEventListener(valueEventListener);
    }

    private static Contractor contractor;
    private Contractor getContractor() {
        return contractor;
    }
    private void putContractor(Contractor contractor) {
        Log.d(TAG, "putting contractor");
        database.child("users").child(uid).setValue(contractor);
        putIntoZipCode(contractor);
    }
    private void putIntoZipCode(Contractor contractor) {
        if(contractor != null && contractor.getContractorDetails() != null && contractor.getContractorDetails().getZipCode() != null) {
            Log.i(TAG, "Putting Contractor into Zipcode:" + contractor.getContractorDetails().getZipCode());
            database.child("zip_codes").child(contractor.getContractorDetails().getZipCode()).child(uid).setValue(contractor.getType());
        }
    }
    private void removeFromOldZipCode(String zipCode) {
        Log.i(TAG, "Removing Contractor from Zipcode:" + zipCode);
        database.child("zip_codes").child(zipCode).child(uid).removeValue();
    }

    //Mark:- Home Page
    public ContractorDetails getContractorDetails() {
        if(getContractor() != null) {
            return getContractor().getContractorDetails();
        }
        return null;
    }
    public void updateContractorDetails(ContractorDetails contractorDetails) {
        if(contractor.getContractorDetails() != null) {
            removeFromOldZipCode(contractor.getContractorDetails().getZipCode());
        }
        contractor.setContractorDetails(contractorDetails);
        putContractor(contractor);
    }
    public String getLogoFileName() {
        if(contractor == null) {
            return null;
        } else {
            return "logos/" + uid + "/" + contractor.getLogoFileName();
        }
    }
    public String setLogoFileName() {
        contractor.setLogoFileName();
        putContractor(contractor);
        return getLogoFileName();
    }

    //Mark:- Customers Page
    public List<String> getCustomers() {
        if(contractor != null && getContractor().getCustomers() != null) {
            return getContractor().getCustomers();
        } else {
            return Collections.emptyList();
        }
    }
    static void addCustomerToContractor(String contractorReference, final String customerReference) {
        final DatabaseReference customerListReference = database.child("users").child(contractorReference).child("customers");
        customerListReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot customerListSnapshot) {
                long listSize = customerListSnapshot.getChildrenCount();
                customerListReference.child(String.valueOf(listSize)).setValue(customerReference);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });
    }
    static void removeCustomerFromContractor(String contractorReference, final String customerReference) {
        DatabaseReference customerListReference = database.child("users").child(contractorReference).child("customers");
        customerListReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot customerListSnapshot) {
                List<String> customers = Collections.emptyList();
                for(DataSnapshot customerSnapshot: customerListSnapshot.getChildren()) {
                    String customer = customerSnapshot.getValue(String.class);
                    if(customer != null && !customer.equals(customerReference)) {
                        customers.add(customer);
                    }
                }
                customerListSnapshot.getRef().setValue(customers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });
    }
    public String getType() {
        if(contractor != null) {
            return getContractor().getType();
        } else {
            return "";
        }
    }

    //Mark:- Settings Page
    public String getNamePrefix() {
        if(contractor != null) {
            return contractor.getNamePrefix();
        } else {
            return "";
        }
    }
    public String getFirstName() {
        if(contractor != null) {
            return contractor.getFirstName();
        } else {
            return "";
        }
    }
    public String getLastName() {
        if(contractor != null) {
            return contractor.getLastName();
        } else {
            return "";
        }
    }
    public void updateProfile(String namePrefix, String firstName, String lastName, String contractorType) {
        if(contractor == null) {
            Log.d(TAG, "customer null");
            contractor = new Contractor();
        }
        contractor.updateProfile(namePrefix, firstName, lastName, contractorType);
        putContractor(contractor);
    }
}
