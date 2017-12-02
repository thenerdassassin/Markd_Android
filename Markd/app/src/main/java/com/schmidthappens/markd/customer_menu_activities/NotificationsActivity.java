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
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
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
    private ListView notificationsListView;
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
        notificationsListView = (ListView)findViewById(R.id.notifications_list_view);
        noNotificationsTextView = (TextView)findViewById(R.id.notifications_empty_list);

        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //notificationsListView.setLayoutManager(layoutManager);
        //notificationsListView.setHasFixedSize(true);
        //notificationsListView.addItemDecoration(new DividerItemDecoration(NotificationsActivity.this, DividerItemDecoration.VERTICAL));
    }

    private void getNotifications(String customerId) {
        if(customerId == null) {
            noNotificationsTextView.setVisibility(View.VISIBLE);
            return;
        }
        NotificationHandler.getNotifications(customerId, notificationValueListener);
    }

    private ValueEventListener notificationValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<String> notifications = new ArrayList<>();
            for (DataSnapshot data: dataSnapshot.getChildren()) {
                notifications.add(data.getValue(String.class));
            }
            Log.d(TAG, "notifications size:" + notifications.size());
            if(notifications.size() > 0) {
                noNotificationsTextView.setVisibility(View.GONE);
            } else {
                noNotificationsTextView.setVisibility(View.VISIBLE);
                // TODO:
                ArrayAdapter<String> adapter = new ArrayAdapter<>(NotificationsActivity.this, android.R.layout.simple_list_item_1, notifications);
                notificationsListView.setAdapter(adapter);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, databaseError.toString());
            Toast.makeText(NotificationsActivity.this, "Oops..something went wrong.", Toast.LENGTH_SHORT).show();
        }
    };
}
