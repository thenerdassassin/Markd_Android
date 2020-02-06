package com.markd.applications.android.home.account_authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.markd.applications.android.home.R;
import com.markd.applications.android.home.contractor_user_activities.ContractorMainActivity;
import com.markd.applications.android.home.customer_menu_activities.MainActivity;
import com.markd.applications.android.home.customer_menu_activities.NotificationsActivity;
import com.markd.applications.android.home.data_objects.FirebaseDatabaseInstance;
import com.markd.applications.android.home.firebase_cloud_messaging.MarkdFirebaseMessagingService;

/**
 * Created by Josh on 8/9/2017.
 */

public class SplashImageActivity extends AppCompatActivity {
    private final static String TAG = "SplashImageActivity";
    FirebaseAuthentication authentication;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabaseInstance.getDatabase();
        setContentView(R.layout.view_splash_image);
        getSupportActionBar().hide();
        authentication = new FirebaseAuthentication(SplashImageActivity.this);
        boolean isLoggedIn = authentication.checkLogin();
        if(isLoggedIn) {
            goToMainActivity();
        } else {
            Intent loginIntent = new Intent(SplashImageActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    public void goToMainActivity() {
        authentication.getUserType(processUserType);
    }

    //Mark:- ValueEventListener
    private ValueEventListener processUserType = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                String userType = dataSnapshot.getValue().toString();
                if (userType.equals("customer")) {
                    if(MarkdFirebaseMessagingService.hasNotifications()) {
                        Intent mainIntent = new Intent(SplashImageActivity.this, NotificationsActivity.class);
                        startActivity(mainIntent);
                    } else {
                        Intent mainIntent = new Intent(SplashImageActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                    }
                    finish();
                } else {
                    Intent contractorMainIntent = new Intent(SplashImageActivity.this, ContractorMainActivity.class);
                    startActivity(contractorMainIntent);
                    finish();
                }
            } else {
                Log.d(TAG, "dataSnapshot Value was null");
                authentication.signOut();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            goToMainActivity();
        }
    };
}