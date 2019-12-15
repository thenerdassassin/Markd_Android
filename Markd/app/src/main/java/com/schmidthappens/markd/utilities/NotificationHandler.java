package com.schmidthappens.markd.utilities;

import androidx.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.data_objects.CustomerNotificationMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 11/24/17.
 */

public class NotificationHandler {
    private final static String TAG = "NotificationsHandler";

    //Mark:- Static methods
    public static void sendNotification(String customerId, final String message, final String companyFrom) {
        final DatabaseReference notificationMessages = FirebaseDatabase.getInstance()
                .getReference("notifications/"+customerId);
        notificationMessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<CustomerNotificationMessage> notifications = new ArrayList<>();
                for(DataSnapshot string: dataSnapshot.getChildren()) {
                    notifications.add(string.getValue(CustomerNotificationMessage.class));
                }
                notifications.add(0, new CustomerNotificationMessage(companyFrom, message));
                notificationMessages.setValue(notifications);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });
    }

    public static void sendNotifications(List<String> customers, final String message, final String companyFrom) {
        if(!StringUtilities.isNullOrEmpty(message) &&
                !StringUtilities.isNullOrEmpty(companyFrom) &&
                customers != null) {
            for (String customer : customers) {
                if (!StringUtilities.isNullOrEmpty(customer)) {
                    sendNotification(customer, message, companyFrom);
                }
            }
        }
    }

    public static boolean getNotifications(String customerId, ValueEventListener listener) {
        if(StringUtilities.isNullOrEmpty(customerId) || listener == null) {
            return false;
        }

        DatabaseReference userReference = FirebaseDatabase.getInstance()
                .getReference().child("notifications").child(customerId);
        userReference.addValueEventListener(listener);
        return true;
    }

    public static void removeNotification(
            final String customerId,
            final int index) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("notifications")
                .child(customerId)
                .child(Integer.toString(index))
                .removeValue();
    }
}
