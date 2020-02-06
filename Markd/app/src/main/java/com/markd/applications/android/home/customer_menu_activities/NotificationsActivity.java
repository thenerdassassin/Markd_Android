package com.markd.applications.android.home.customer_menu_activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.markd.applications.android.home.AdapterClasses.NotificationRecyclerSwipeViewAdapter;
import com.markd.applications.android.home.R;
import com.markd.applications.android.home.account_authentication.FirebaseAuthentication;
import com.markd.applications.android.home.account_authentication.LoginActivity;
import com.markd.applications.android.home.data_objects.CustomerNotificationMessage;
import com.markd.applications.android.home.firebase_cloud_messaging.MarkdFirebaseMessagingService;
import com.markd.applications.android.home.utilities.NotificationHandler;
import com.markd.applications.android.home.view_initializers.ActionBarInitializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh Schmidt on 11/28/17.
 */

public class NotificationsActivity extends AppCompatActivity {
    public static final String TAG = "NotificationsActivity";
    FirebaseAuthentication authentication;
    private RecyclerView notificationRecyclerView;
    private TextView noNotificationsTextView;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_notifications_view);
        authentication = new FirebaseAuthentication(this);
        new ActionBarInitializer(this, true, "customer");
        initializeViews();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(authentication.getCurrentUser() != null) {
            getNotifications(authentication.getCurrentUser().getUid());
        }
        MarkdFirebaseMessagingService.resetBadgeCount(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
    }

    private void initializeViews() {
        notificationRecyclerView = findViewById(R.id.notifications_list_view);
        noNotificationsTextView = findViewById(R.id.notifications_empty_list);
    }
    private void setUpRecyclerView(List<CustomerNotificationMessage> notifications) {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notificationRecyclerView.setLayoutManager(layoutManager);
        notificationRecyclerView.setHasFixedSize(false);
        notificationRecyclerView
                .addItemDecoration(
                        new DividerItemDecoration(
                                NotificationsActivity.this,
                                DividerItemDecoration.VERTICAL));
        notificationRecyclerView.setAdapter(
                new NotificationRecyclerSwipeViewAdapter(
                        this,
                        authentication.getCurrentUser().getUid(),
                        notifications));
    }
    private void getNotifications(String customerId) {
        if(customerId == null
                || !NotificationHandler.getNotifications(customerId, notificationValueListener)) {
            noNotificationsTextView.setVisibility(View.VISIBLE);
        } else {
            noNotificationsTextView.setVisibility(View.GONE);
        }
    }
    private ValueEventListener notificationValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<CustomerNotificationMessage> notifications = new ArrayList<>();
            for (DataSnapshot data: dataSnapshot.getChildren()) {
                Log.d(TAG, data.toString());
                notifications.add(data.getValue(CustomerNotificationMessage.class));
            }
            Log.d(TAG, "notifications size:" + notifications.size());
            if(notifications.size() > 0) {
                noNotificationsTextView.setVisibility(View.GONE);
                setUpRecyclerView(notifications);
            } else {
                noNotificationsTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, databaseError.toString());
            Toast.makeText(
                    NotificationsActivity.this,
                    "Oops..something went wrong.",
                    Toast.LENGTH_SHORT).show();
        }
    };
}