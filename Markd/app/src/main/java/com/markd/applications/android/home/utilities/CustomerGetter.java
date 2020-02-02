package com.markd.applications.android.home.utilities;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.markd.applications.android.home.data_objects.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Josh Schmidt on 11/22/17.
 */

public class CustomerGetter {
    private final static String TAG = "CustomerGetter";
    public static Map<String, Customer> getCustomersFromReferences(List<String> customerReferences, DataSnapshot usersSnapshot) {
        if(customerReferences == null) {
            Log.d(TAG, "customerReferences is null");
            return new LinkedHashMap<>();
        }
        final List<Customer> customers = new ArrayList<>();
        Map<String, Customer> unsortedCustomerMap = new TreeMap<>();
        for(String customerKey: customerReferences) {
            Log.d(TAG, "customerReferences:" + customerReferences.toString());
            if(StringUtilities.isNullOrEmpty(customerKey)) {
                continue;
            }
            DataSnapshot customer = usersSnapshot.child(customerKey);
            if (customer.exists()) {
                customers.add(customer.getValue(Customer.class));
                unsortedCustomerMap.put(customerKey, customer.getValue(Customer.class));
            }
        }
        List<Map.Entry<String, Customer>> mapEntryList = new LinkedList<Map.Entry<String, Customer>>(unsortedCustomerMap.entrySet());
        Collections.sort(mapEntryList, new LastNameNapeComparator());
        //Collections.sort(customers, new LastNameComparator());

        Map<String, Customer> sortedCustomerMap = new LinkedHashMap<>();
        for (Map.Entry<String, Customer> entry : mapEntryList) {
            sortedCustomerMap.put(entry.getKey(), entry.getValue());
        }

        return sortedCustomerMap;
    }

    private static class LastNameComparator implements Comparator<Customer> {
        @Override
        public int compare(Customer o1, Customer o2) {
            return o1.getLastName().compareToIgnoreCase(o2.getLastName());
        }
    }

    private static class LastNameNapeComparator implements Comparator<Map.Entry<String, Customer>> {
        @Override
        public int compare(Map.Entry<String, Customer> o1, Map.Entry<String, Customer> o2) {
            LastNameComparator comparator = new LastNameComparator();
            return comparator.compare(o1.getValue(), o2.getValue());
        }
    }
}
