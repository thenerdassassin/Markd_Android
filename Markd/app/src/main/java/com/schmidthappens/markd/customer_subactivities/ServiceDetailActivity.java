package com.schmidthappens.markd.customer_subactivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_menu_activities.ElectricalActivity;
import com.schmidthappens.markd.customer_menu_activities.HvacActivity;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.customer_menu_activities.PlumbingActivity;
import com.schmidthappens.markd.data_objects.ContractorService;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.DateUtitilities;

/**
 * Created by Josh on 8/5/2017.
 */

public class ServiceDetailActivity extends AppCompatActivity {
    String TAG = "ServiceDetailActivity";
    FirebaseAuthentication authentication;
    TempCustomerData customerData;
    AlertDialog alertDialog;

    //XML Objects
    EditText editContractor;
    EditText editServiceDescription;
    Button saveButton;
    Button deleteButton;

    int serviceId;
    boolean isNew;
    boolean isContractorEditingPage;

    Class<?> originalActivity;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.edit_view_service);
        authentication = new FirebaseAuthentication(this);

        setTitle("Edit Service");
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(NullPointerException e) {
            sendErrorMessage("getSupportActionBar returned Null");
        }

        initializeXMLObjects();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        processIntent(getIntent());
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    private void saveServiceData() {
        if(isNew) {
            ContractorService serviceToAdd = new ContractorService(DateUtitilities.getCurrentMonth()+1, DateUtitilities.getCurrentDay(), DateUtitilities.getCurrentYear(),
                    editContractor.getText().toString(), editServiceDescription.getText().toString());
            addService(serviceToAdd);

        } else {
            updateService(serviceId, editContractor.getText().toString(), editServiceDescription.getText().toString());
        }
    }
    private void addService(ContractorService service) {
        if (originalActivity.equals(PlumbingActivity.class)) {
            Log.d(TAG, "Add Plumbing Service");
            customerData.addPlumbingService(service);
        } else if (originalActivity.equals(HvacActivity.class)) {
            Log.d(TAG, "Add Hvac Service");
            customerData.addHvacService(service);
        } else if (originalActivity.equals(ElectricalActivity.class)) {
            Log.d(TAG, "Add Electrical Service");
            customerData.addElectricalService(service);
        } else {
            sendErrorMessage("Activity does not match");
        }
    }
    private void updateService(int serviceId, String contractor, String description) {
        if (originalActivity.equals(PlumbingActivity.class)) {
            Log.d(TAG, "Edit Plumbing Service");
            customerData.updatePlumbingService(serviceId, contractor, description);
        } else if (originalActivity.equals(HvacActivity.class)) {
            Log.d(TAG, "Edit Hvac Service");
            customerData.updateHvacService(serviceId, contractor, description);
        } else if (originalActivity.equals(ElectricalActivity.class)) {
            Log.d(TAG, "Edit Electrical Service");
            customerData.updateElectricalService(serviceId, contractor, description);
        } else {
            sendErrorMessage("Activity does not match");
        }
    }
    private void showRemoveServiceWarning() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Service")
                .setMessage("This action can not be reversed. Are you sure you want to delete this service?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Cancel button
                        Log.d(TAG, "Cancel the service deletions");
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Delete button
                        Log.d(TAG, "Confirm service deletion");
                        removeService();
                        dialog.dismiss();
                        goBackToActivity(originalActivity);
                    }
                })
                .create();
        alertDialog.show();
    }
    private void removeService() {
        if (originalActivity.equals(PlumbingActivity.class)) {
            customerData.removePlumbingService(serviceId);
        } else if (originalActivity.equals(HvacActivity.class)) {
            customerData.removeHvacService(serviceId);
        } else if (originalActivity.equals(ElectricalActivity.class)) {
            customerData.removeElectricalService(serviceId);
        } else {
            sendErrorMessage("Activity does not match");
        }
    }

    private void initializeXMLObjects() {
        editContractor = (EditText)findViewById(R.id.service_edit_contractor);
        setEnterButtonToKeyboardDismissal(editContractor);
        editServiceDescription = (EditText)findViewById(R.id.service_edit_description);
        setEnterButtonToKeyboardDismissal(editServiceDescription);
        
        //Used to show done button but still allow multiple lines
        editServiceDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editServiceDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);

        saveButton = (Button)findViewById(R.id.service_edit_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(ServiceDetailActivity.this.getCurrentFocus());
                saveServiceData();
                goBackToActivity(originalActivity);
            }
        });

        deleteButton = (Button)findViewById(R.id.service_edit_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(ServiceDetailActivity.this.getCurrentFocus());
                if(!isNew) {
                    showRemoveServiceWarning();
                } else {
                    goBackToActivity(originalActivity);
                }
            }
        });
    }
    private void processIntent(Intent intent) {
        if(intent != null) {
            if(intent.hasExtra("originalActivity")) {
                originalActivity = (Class<?>)intent.getSerializableExtra("originalActivity");
            }

            isContractorEditingPage = intent.getBooleanExtra("isContractor", false);
            if(intent.hasExtra("customerId")) {
                customerData = new TempCustomerData(intent.getStringExtra("customerId"), null);
            } else {
                sendErrorMessage("customerId is null");
            }

            if(intent.hasExtra("contractor")) {
                editContractor.setText(intent.getStringExtra("contractor"));
            }

            //defaults to false if "isNew" does not exist
            isNew = intent.getBooleanExtra("isNew", false);
            if(!isNew) {
                if(intent.hasExtra("serviceId")) {
                    serviceId = Integer.parseInt(intent.getStringExtra("serviceId"));
                } else {
                    sendErrorMessage("Service Id doesn't exist");
                }
                if(intent.hasExtra("description")) {
                    editServiceDescription.setText(intent.getStringExtra("description"));
                }
            }
        } else {
            sendErrorMessage("Intent is Null");
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Log.i(TAG, "Navigate Up");
        hideKeyboard(this.getCurrentFocus());
        goBackToActivity(originalActivity);
        finish();
        return true;
    }
    private void goBackToActivity(Class<?> originalActivity){
        Intent originalActivityIntent = new Intent(getApplicationContext(), originalActivity);
        Log.d(TAG, "isContractor:" + isContractorEditingPage);
        originalActivityIntent.putExtra("isContractor", isContractorEditingPage);
        originalActivityIntent.putExtra("customerId", customerData.getUid());
        hideKeyboard(this.getCurrentFocus());
        startActivity(originalActivityIntent);
        finish();
    }
    //Makes the enter button dismiss soft keyboard
    private void setEnterButtonToKeyboardDismissal(final EditText view) {
        view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });
    }
    //Hides Keyboard
    private void hideKeyboard(View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private void sendErrorMessage(String message) {
        Log.e(TAG, message);
        Toast.makeText(getApplicationContext(), "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
        goBackToActivity(MainActivity.class);
    }
}
