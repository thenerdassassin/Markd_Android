package com.schmidthappens.markd.utilities;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 11/24/17.
 */

public class NotificationHandler {
    private final static String TAG = "NotificationsHandler";

    //Mark:- Static methods
    public static void sendNotification(String customerId, final String message) {
        final DatabaseReference notificationMessages = FirebaseDatabase.getInstance().getReference("notifications/"+customerId);
        notificationMessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> notifications = new ArrayList<>();
                for(DataSnapshot string: dataSnapshot.getChildren()) {
                    notifications.add(string.getValue(String.class));
                }
                notifications.add(0, message);
                //Limit notifications to last 10
                if(notifications.size() > 10) {
                    notifications = notifications.subList(0, 10);
                }
                notificationMessages.setValue(notifications);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });
    }
}
