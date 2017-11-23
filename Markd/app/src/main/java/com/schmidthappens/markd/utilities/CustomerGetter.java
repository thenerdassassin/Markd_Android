package com.schmidthappens.markd.utilities;

import com.google.firebase.database.DataSnapshot;
import com.schmidthappens.markd.data_objects.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 11/22/17.
 */

public class CustomerGetter {
    public static List<Customer> getCustomersFromReferences(List<String> customerReferences, DataSnapshot usersSnapshot) {
        final List<Customer> customers = new ArrayList<>();
        for(String customerKey: customerReferences) {
            DataSnapshot customer = usersSnapshot.child(customerKey);
            if (customer.exists()) {
                customers.add(customer.getValue(Customer.class));
            }
        }
        return customers;
    }
}
