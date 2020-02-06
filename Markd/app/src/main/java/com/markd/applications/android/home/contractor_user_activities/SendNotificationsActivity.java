package com.markd.applications.android.home.contractor_user_activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.markd.applications.android.home.R;
import com.markd.applications.android.home.account_authentication.FirebaseAuthentication;
import com.markd.applications.android.home.account_authentication.LoginActivity;
import com.markd.applications.android.home.data_objects.TempContractorData;
import com.markd.applications.android.home.utilities.NotificationHandler;
import com.markd.applications.android.home.utilities.StringUtilities;

/**
 * Created by Josh Schmidt on 11/24/17.
 */

public class SendNotificationsActivity extends AppCompatActivity{
    private final static String TAG = "SendNotificationsActvy";
    private final static String SENDTOALL = "All";
    FirebaseAuthentication authentication;
    TempContractorData contractorData;
    String customerId;

    EditText notificationMessage;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contractor_notifications_activity);
        authentication = new FirebaseAuthentication(this);
        setTitle("Push Notification");
        initializeXMLObjects();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        contractorData = new TempContractorData((authentication.getCurrentUser().getUid()), null);
        processIntent(getIntent());
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
    }

    private void initializeXMLObjects() {
        notificationMessage = (EditText)findViewById(R.id.notification_message);
        sendButton = (Button)findViewById(R.id.notification_send_button);
        sendButton.setOnClickListener(sendNotificationClickListener);
    }
    private void processIntent(Intent intent) {
        if(intent != null) {
            if(intent.hasExtra("customerId")) {
                this.customerId = intent.getStringExtra("customerId");
            } else {
                this.customerId = SENDTOALL;
            }
        }
    }

    private View.OnClickListener sendNotificationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String message = notificationMessage.getText().toString();
            if(!StringUtilities.isNullOrEmpty(message)) {
                if(customerId.equalsIgnoreCase(SENDTOALL)) {
                    Log.i(TAG, "Add to All Customer {message:" + message + "}");
                    sendNotifications(message);
                } else {
                    Log.i(TAG, "Add Notification:{ user:" + customerId + ", message:" + message + "}");
                    sendNotification(message);
                }
                Toast.makeText(SendNotificationsActivity.this, "Notifications Sent.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(SendNotificationsActivity.this, "Can not send empty message.", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void sendNotification(String message) {
        if(contractorData != null && contractorData.getContractorDetails() != null && contractorData.getContractorDetails().getCompanyName() != null) {
            NotificationHandler.sendNotification(customerId, message, contractorData.getContractorDetails().getCompanyName());
        }
    }
    private void sendNotifications(String message) {
        if(contractorData != null && contractorData.getCustomers() != null && contractorData.getContractorDetails() != null && contractorData.getContractorDetails().getCompanyName() != null) {
            NotificationHandler.sendNotifications(contractorData.getCustomers(), message, contractorData.getContractorDetails().getCompanyName());
        }
    }
}