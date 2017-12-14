package com.schmidthappens.markd.customer_menu_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.AdapterClasses.NotificationRecyclerViewAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.data_objects.CustomerNotificationMessage;
import com.schmidthappens.markd.utilities.NotificationHandler;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 11/28/17.
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
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
    }

    private void initializeViews() {
        notificationRecyclerView = (RecyclerView)findViewById(R.id.notifications_list_view);
        noNotificationsTextView = (TextView)findViewById(R.id.notifications_empty_list);
    }
    private void setUpRecyclerView(List<CustomerNotificationMessage> notifications) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notificationRecyclerView.setLayoutManager(layoutManager);
        notificationRecyclerView.setHasFixedSize(false);
        notificationRecyclerView.addItemDecoration(new DividerItemDecoration(NotificationsActivity.this, DividerItemDecoration.VERTICAL));
        notificationRecyclerView.setAdapter(new NotificationRecyclerViewAdapter(notifications));
    }
    private void getNotifications(String customerId) {
        if(customerId == null || !NotificationHandler.getNotifications(customerId, notificationValueListener)) {
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
            Toast.makeText(NotificationsActivity.this, "Oops..something went wrong.", Toast.LENGTH_SHORT).show();
        }
    };
}
