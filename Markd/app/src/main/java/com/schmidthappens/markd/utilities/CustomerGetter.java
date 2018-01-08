package com.schmidthappens.markd.utilities;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.schmidthappens.markd.data_objects.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
            if(StringUtilities.isNullOrEmpty(customerKey)) {
                continue;
            }
            DataSnapshot customer = usersSnapshot.child(customerKey);
            if (customer.exists()) {
                customers.add(customer.getValue(Customer.class));
            }
        }
        Collections.sort(customers, new LastNameComparator());
        return customers;
    }

    private static class LastNameComparator implements Comparator<Customer> {
        @Override
        public int compare(Customer o1, Customer o2) {
            return o1.getLastName().compareToIgnoreCase(o2.getLastName());
        }
    }
}
