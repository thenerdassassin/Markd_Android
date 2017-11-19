package com.schmidthappens.markd.utilities;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.AdapterClasses.ContractorListRecyclerViewAdapter;
import com.schmidthappens.markd.customer_subactivities.ChangeContractorActivity;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by joshua.schmidtibm.com on 11/18/17.
 */

public class ContractorSearch {
    private static final String TAG = "ContractorSearch";

    /*
        Returns a list of Contractor Database Keys in zipCodes with the specified contractorType
     */
    public static List<String> getContractorsInZipCodes(final Map<Double, String> zipCodeMap, final String contractorType, final DataSnapshot firebaseZipCodesSnapshot) {
        List<String> contractors = new ArrayList<>();

        for (Map.Entry<Double, String> zipCode : zipCodeMap.entrySet()) {
            DataSnapshot listOfContractorsAtZipCode = firebaseZipCodesSnapshot.child(zipCode.getValue());
            if (listOfContractorsAtZipCode.exists()) {
                for (DataSnapshot contractorReference : listOfContractorsAtZipCode.getChildren()) {
                    String contractorReferenceType = contractorReference.getValue().toString();
                    if (contractorReferenceType.equals(contractorType)) {
                        contractors.add(contractorReference.getKey());
                    }
                }
            }
        }
        Log.d(TAG, contractors.toString());
        return contractors;
    }

    public static List<Contractor> getContractorsFromReferences(List<String> contractorReferences, DataSnapshot usersSnapshot) {
        final List<Contractor> contractors = new ArrayList<>();
        for(String contractorKey: contractorReferences) {
            DataSnapshot contractor = usersSnapshot.child(contractorKey);
            if (contractor.exists()) {
                DataSnapshot contractorDetails = contractor.child("contractorDetails");
                if(contractorDetails.exists()) {
                    ContractorDetails details = contractorDetails.getValue(ContractorDetails.class);
                    if(details != null) {
                        contractors.add(contractor.getValue(Contractor.class));
                    }
                }
            }
        }
        return contractors;
    }
}
