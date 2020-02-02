package com.markd.applications.android.home.customer_menu_activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.markd.applications.android.home.R;
import com.markd.applications.android.home.account_authentication.FirebaseAuthentication;
import com.markd.applications.android.home.account_authentication.LoginActivity;
import com.markd.applications.android.home.customer_subactivities.ChangeContractorActivity;
import com.markd.applications.android.home.customer_subactivities.HelpActivity;
import com.markd.applications.android.home.customer_subactivities.HomeEditActivity;
import com.markd.applications.android.home.customer_subactivities.ProfileEditActivity;
import com.markd.applications.android.home.data_objects.TempCustomerData;
import com.markd.applications.android.home.view_initializers.ActionBarInitializer;

/**
 * Created by Josh Schmidt on 10/21/17.
 */

public class SettingsActivity extends AppCompatActivity {
    //TODO: Add link to manage subscription:
    // https://developer.android.com/google/play/billing/billing_subscriptions#deep-links-manage-subs
    public static final String TAG = "SettingsActivity";
    FirebaseAuthentication authentication;
    TempCustomerData customerData;

    RelativeLayout edit_contractors;
    RelativeLayout edit_home;
    RelativeLayout edit_profile;
    RelativeLayout contact_us;
    RelativeLayout edit_password;
    RelativeLayout sign_out;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_settings_view);
        authentication = new FirebaseAuthentication(this);

        new ActionBarInitializer(this, true, "customer");
        initializeUI();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        customerData = new TempCustomerData(this, null);
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }

    private void initializeUI() {
        //Hide Contractor only Settings
        final RelativeLayout edit_company = findViewById(R.id.edit_company);
        edit_company.setVisibility(View.GONE);

        edit_contractors = findViewById(R.id.edit_contractors);
        edit_contractors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Start ChangeContractorActivity");
                startActivity(createChangeContractorActivity());
            }
        });
        edit_home = findViewById(R.id.edit_home);
        edit_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Start HomeEditActivity");
                startActivity(createEditHomeIntent());
            }
        });
        edit_profile = findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Start ProfileEditActivity");
                startActivity(createEditProfileIntent());
            }
        });
        contact_us = findViewById(R.id.contact_us);
        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Start HelpActivity");
                startActivity(new Intent(
                        SettingsActivity.this,
                        HelpActivity.class));
            }
        });
        edit_password = findViewById(R.id.edit_password);
        edit_password.setOnClickListener(editPasswordClickListener);
        sign_out = findViewById(R.id.sign_out);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Sign Out");
                new FirebaseAuthentication(SettingsActivity.this).signOut();
            }
        });
    }

    private Intent createEditProfileIntent() {
        Context context = SettingsActivity.this;
        Class destinationClass = ProfileEditActivity.class;
        Intent intentToStartProfileEditActivity = new Intent(context, destinationClass);

        intentToStartProfileEditActivity.putExtra("email", authentication.getCurrentUser().getEmail());
        intentToStartProfileEditActivity.putExtra("namePrefix", customerData.getNamePrefix());
        intentToStartProfileEditActivity.putExtra("firstName", customerData.getFirstName());
        intentToStartProfileEditActivity.putExtra("lastName", customerData.getLastName());
        intentToStartProfileEditActivity.putExtra("maritalStatus", customerData.getMaritalStatus());
        return intentToStartProfileEditActivity;
    }
    private Intent createEditHomeIntent() {
        Context context = SettingsActivity.this;
        Class destinationClass = HomeEditActivity.class;
        Intent intentToStartHomeEditActivity = new Intent(context, destinationClass);

        intentToStartHomeEditActivity.putExtra("street", customerData.getStreet());
        intentToStartHomeEditActivity.putExtra("city", customerData.getCity());
        intentToStartHomeEditActivity.putExtra("state", customerData.getState());
        intentToStartHomeEditActivity.putExtra("zipcode", customerData.getZipcode());
        intentToStartHomeEditActivity.putExtra("bedrooms", customerData.getBedrooms());
        intentToStartHomeEditActivity.putExtra("bathrooms", customerData.getBathrooms());
        intentToStartHomeEditActivity.putExtra("squareFootage", customerData.getSquareFootage());
        return intentToStartHomeEditActivity;
    }
    private Intent createChangeContractorActivity() {
        Context context = SettingsActivity.this;
        Class destinationClass = ChangeContractorActivity.class;
        return new Intent(context, destinationClass);
    }
    private View.OnClickListener editPasswordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "Send Password Reset");
            (new AlertDialog.Builder(SettingsActivity.this)
                    .setMessage(R.string.password_email_dialog_message)
                    .setTitle(R.string.password_email_dialog_title)
                    .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            authentication.sendPasswordResetEmail(SettingsActivity.this, authentication.getCurrentUser().getEmail());
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    })
                    .create())
                    .show();
        }
    };
}
