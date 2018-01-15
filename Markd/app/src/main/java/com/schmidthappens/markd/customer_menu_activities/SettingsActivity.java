package com.schmidthappens.markd.customer_menu_activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_subactivities.ChangeContractorActivity;
import com.schmidthappens.markd.customer_subactivities.HomeEditActivity;
import com.schmidthappens.markd.customer_subactivities.ProfileEditActivity;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;

import java.util.Set;

/**
 * Created by joshua.schmidtibm.com on 10/21/17.
 */

public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = "SettingsActivity";
    FirebaseAuthentication authentication;
    TempCustomerData customerData;

    RelativeLayout edit_profile;
    RelativeLayout edit_home;
    RelativeLayout edit_contractor;
    RelativeLayout edit_password;

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
        edit_profile = (RelativeLayout)findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Start ProfileEditActivity");
                startActivity(createEditProfileIntent());
            }
        });

        edit_home = (RelativeLayout)findViewById(R.id.edit_home);
        edit_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Start HomeEditActivity");
                startActivity(createEditHomeIntent());
            }
        });

        edit_contractor = (RelativeLayout)findViewById(R.id.edit_contractors);
        edit_contractor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Start ChangeContractorActivity");
                startActivity(createChangeContractorActivity());
            }
        });

        edit_password = (RelativeLayout)findViewById(R.id.edit_password);
        edit_password.setOnClickListener(editPasswordClickListener);
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
        Intent intentToStartChangeContractorActivity = new Intent(context, destinationClass);

        return intentToStartChangeContractorActivity;
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
