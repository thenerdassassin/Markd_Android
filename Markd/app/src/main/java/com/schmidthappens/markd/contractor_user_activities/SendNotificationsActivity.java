package com.schmidthappens.markd.contractor_user_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.data_objects.TempContractorData;
import com.schmidthappens.markd.utilities.NotificationHandler;
import com.schmidthappens.markd.utilities.StringUtilities;

/**
 * Created by joshua.schmidtibm.com on 11/24/17.
 */

public class SendNotificationsActivity extends AppCompatActivity{
    private final static String TAG = "SendNotificationsActvy";
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
            }
        }
    }

    private View.OnClickListener sendNotificationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String message = notificationMessage.getText().toString();
            if(!StringUtilities.isNullOrEmpty(message)) {
                Log.i(TAG, "Add Notification:{ user:" + customerId + ", message:" + message + "}");
                Toast.makeText(SendNotificationsActivity.this, "Send Notifications", Toast.LENGTH_SHORT).show();
                if(contractorData != null && contractorData.getContractorDetails() != null && contractorData.getContractorDetails().getCompanyName() != null) {
                    NotificationHandler.sendNotification(customerId, message, contractorData.getContractorDetails().getCompanyName());
                }
                finish();
            }
        }
    };
}
