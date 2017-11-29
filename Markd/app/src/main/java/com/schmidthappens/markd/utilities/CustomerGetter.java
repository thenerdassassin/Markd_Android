package com.schmidthappens.markd.utilities;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.schmidthappens.markd.data_objects.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 11/22/17.
 */

public class CustomerGetter {
    private final static String TAG = "CustomerGetter";
    public static List<Customer> getCustomersFromReferences(List<String> customerReferences, DataSnapshot usersSnapshot) {
        if(customerReferences == null) {
            Log.d(TAG, "customerReferences is null");
            return Collections.emptyList();
        }
        final List<Customer> customers = new ArrayList<>();
        for(String customerKey: customerReferences) {
            Log.d(TAG, "customerReferences:" + customerReferences.toString());
            DataSnapshot customer = usersSnapshot.child(customerKey);
            if (customer.exists()) {
                customers.add(customer.getValue(Customer.class));
            }
        }
        return customers;
    }
}
