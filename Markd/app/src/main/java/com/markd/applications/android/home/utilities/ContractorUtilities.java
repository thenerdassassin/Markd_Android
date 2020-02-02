package com.markd.applications.android.home.utilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.markd.applications.android.home.customer_menu_activities.ElectricalActivity;
import com.markd.applications.android.home.customer_menu_activities.HvacActivity;
import com.markd.applications.android.home.customer_menu_activities.PaintingActivity;
import com.markd.applications.android.home.customer_menu_activities.PlumbingActivity;
import com.markd.applications.android.home.data_objects.Contractor;
import com.markd.applications.android.home.data_objects.ContractorDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Josh Schmidt on 11/18/17.
 */

public class ContractorUtilities {
    private static final String TAG = "ContractorUtilities";

    /*
        Returns a list of Contractor Database Keys in zipCodes with the specified contractorType
     */
    public static List<String> getContractorsInZipCodes(final Map<Double, String> zipCodeMap, final String contractorType, final DataSnapshot firebaseZipCodesSnapshot) {
        List<String> contractors = new ArrayList<>();

        for (Map.Entry<Double, String> zipCode : zipCodeMap.entrySet()) {
            DataSnapshot listOfContractorsAtZipCode = firebaseZipCodesSnapshot.child(zipCode.getValue());
            Log.v(TAG, listOfContractorsAtZipCode.toString());
            if (listOfContractorsAtZipCode.exists()) {
                Log.i(TAG, "contractors at zipcode:" + zipCode.getValue());
                for (DataSnapshot contractorReference : listOfContractorsAtZipCode.getChildren()) {
                    String contractorReferenceType = contractorReference.getValue(String.class);
                    if (contractorReferenceType != null && contractorReferenceType.equals(contractorType)) {
                        contractors.add(contractorReference.getKey());
                    }
                }
            } else {
                Log.i(TAG, "No contractors at zipcode:" + zipCode.getValue());
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

    @Nullable
    public static Class getClassFromContractorType(@NonNull String contractorType) {
        switch (contractorType.toLowerCase()){
            case("plumber"):
                return PlumbingActivity.class;
            case("electrician"):
                return ElectricalActivity.class;
            case("painter"):
                return PaintingActivity.class;
            case("hvac"):
                return HvacActivity.class;
            default:
                Log.e(TAG, "No match for userType:" + contractorType);
                return null;
        }
    }
}
