package com.schmidthappens.markd.contractor_user_activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_menu_activities.SettingsActivity;
import com.schmidthappens.markd.customer_subactivities.ChangeContractorActivity;
import com.schmidthappens.markd.customer_subactivities.HomeEditActivity;
import com.schmidthappens.markd.customer_subactivities.ProfileEditActivity;
import com.schmidthappens.markd.data_objects.TempContractorData;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;

/**
 * Created by joshua.schmidtibm.com on 1/13/18.
 */

public class ContractorSettingsActivity extends AppCompatActivity {
    public static final String TAG = "ContractorSettings";
    FirebaseAuthentication authentication;
    TempContractorData contractorData;

    RelativeLayout edit_profile;
    RelativeLayout edit_password;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_settings_view);

        authentication = new FirebaseAuthentication(this);
        new ActionBarInitializer(this, false, "contractor");

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
        contractorData = new TempContractorData(this, null);
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
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

        RelativeLayout edit_home = (RelativeLayout)findViewById(R.id.edit_home);
        edit_home.setVisibility(View.GONE);

        RelativeLayout edit_contractor = (RelativeLayout)findViewById(R.id.edit_contractors);
        edit_contractor.setVisibility(View.GONE);

        edit_password = (RelativeLayout)findViewById(R.id.edit_password);
        edit_password.setOnClickListener(editPasswordClickListener);
    }

    private Intent createEditProfileIntent() {
        Context context = ContractorSettingsActivity.this;
        Class destinationClass = ProfileEditActivity.class;
        Intent intentToStartProfileEditActivity = new Intent(context, destinationClass);

        intentToStartProfileEditActivity.putExtra("email", authentication.getCurrentUser().getEmail());
        intentToStartProfileEditActivity.putExtra("namePrefix", contractorData.getNamePrefix());
        intentToStartProfileEditActivity.putExtra("firstName", contractorData.getFirstName());
        intentToStartProfileEditActivity.putExtra("lastName", contractorData.getLastName());
        intentToStartProfileEditActivity.putExtra("contractorType", contractorData.getType());

        return intentToStartProfileEditActivity;
    }
    private View.OnClickListener editPasswordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "Send Password Reset");
            (new AlertDialog.Builder(ContractorSettingsActivity.this)
                    .setMessage(R.string.password_email_dialog_message)
                    .setTitle(R.string.password_email_dialog_title)
                    .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            authentication.sendPasswordResetEmail(ContractorSettingsActivity.this, authentication.getCurrentUser().getEmail());
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
