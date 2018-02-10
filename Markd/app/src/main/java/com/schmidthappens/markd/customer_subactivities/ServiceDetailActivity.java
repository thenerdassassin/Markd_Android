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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_menu_activities.ElectricalActivity;
import com.schmidthappens.markd.customer_menu_activities.HvacActivity;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.customer_menu_activities.PlumbingActivity;
import com.schmidthappens.markd.data_objects.ContractorService;
import com.schmidthappens.markd.data_objects.Customer;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.file_storage.FirebaseFile;
import com.schmidthappens.markd.file_storage.MarkdFirebaseStorage;
import com.schmidthappens.markd.utilities.DateUtitilities;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.view_initializers.ServiceFileListViewInitializer;
import com.schmidthappens.markd.view_initializers.ServiceListViewInitializer;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Josh on 8/5/2017.
 */

public class ServiceDetailActivity extends AppCompatActivity {
    private final static String TAG = "ServiceDetailActivity";
    FirebaseAuthentication authentication;
    TempCustomerData customerData;
    AlertDialog alertDialog;

    //XML Objects
    EditText editContractor;
    EditText editServiceDescription;
    FrameLayout fileList;
    Button saveButton;
    Button deleteButton;

    boolean isContractorEditingPage;
    Class<?> originalActivity;
    int serviceId;
    boolean isAddService;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.edit_view_service);
        authentication = new FirebaseAuthentication(this);

        setTitle("Edit Service");
        if(getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
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

    private String getServiceType() {
        String serviceType = null;
        if (originalActivity.equals(PlumbingActivity.class)) {
            Log.d(TAG, "Plumbing Service");
            serviceType = "plumber";
        } else if (originalActivity.equals(HvacActivity.class)) {
            Log.d(TAG, "Hvac Service");
            serviceType = "hvac";
        } else if (originalActivity.equals(ElectricalActivity.class)) {
            Log.d(TAG, "Electrical Service");
            serviceType = "electrician";
        }
        return serviceType;
    }
    private void saveServiceData() {
        if(serviceId < 0) {
            //New Service
            ContractorService serviceToAdd = new ContractorService(DateUtitilities.getCurrentMonth()+1, DateUtitilities.getCurrentDay(), DateUtitilities.getCurrentYear(),
                    editContractor.getText().toString(), editServiceDescription.getText().toString(), null);
            addService(serviceToAdd);
        } else {
            updateService(serviceId, editContractor.getText().toString(), editServiceDescription.getText().toString(), customerData.getServices(getServiceType()).get(serviceId).getFiles());
        }
    }
    private void addService(ContractorService service) {
        String serviceType = getServiceType();
        if(serviceType == null) {
            sendErrorMessage("Activity does not match");
        } else {
            Log.d(TAG, "Add " + serviceType + "service");
            customerData.addService(service, serviceType);
        }
    }
    private void updateService(int serviceId, String contractor, String description, List<FirebaseFile> files) {
        String serviceType = getServiceType();
        if(serviceType == null) {
            sendErrorMessage("Activity does not match");
        } else {
            Log.d(TAG, "Edit " + serviceType + "service-" + serviceId);
            customerData.updateService(serviceId, contractor, description, files, serviceType);
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
        String serviceType = getServiceType();
        if(serviceType == null) {
            sendErrorMessage("Activity does not match");
        } else {
            Log.d(TAG, "Remove " + serviceType + "service-" + serviceId);
            List<FirebaseFile> files = customerData.getServices(getServiceType()).get(serviceId).getFiles();
            removeFiles(customerData.getUid(), files);
            customerData.removeService(serviceId, serviceType);
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

        fileList = (FrameLayout)findViewById(R.id.service_file_list);

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
                if(serviceId < 0) {
                    goBackToActivity(originalActivity);

                } else {
                    showRemoveServiceWarning();
                }
            }
        });
    }
    private void processIntent(final Intent intent) {
        Log.d(TAG, "Processing intent");
        if(intent != null) {
            if(intent.hasExtra("originalActivity")) {
                originalActivity = (Class<?>)intent.getSerializableExtra("originalActivity");
            }

            isContractorEditingPage = intent.getBooleanExtra("isContractor", false);

            if(intent.hasExtra("contractor")) {
                editContractor.setText(intent.getStringExtra("contractor"));
                editContractor.setSelection(editContractor.getText().length());
            }

            serviceId = intent.getIntExtra("serviceId", -1);
            if(serviceId < 0) {
                isAddService = true;
            } else {
                //Editing Service
                if(intent.hasExtra("description")) {
                    editServiceDescription.setText(intent.getStringExtra("description"));
                }
            }

            customerData = new TempCustomerData(intent.getStringExtra("customerId"), null);
            customerData.attachListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<FirebaseFile> files = new ArrayList<>();
                    if(isAddService) {
                        //Create New Service if new to be able to store information
                        saveServiceData();
                        serviceId = 0;
                        intent.putExtra("serviceId", 0);
                    } else {
                        if(customerData.getServices(getServiceType()) != null) {
                            ContractorService service = customerData.getServices(getServiceType()).get(serviceId);
                            if(service != null) {
                                files = service.getFiles();
                            }
                        }

                    }
                    fileList.removeAllViews();
                    fileList.addView(
                            ServiceFileListViewInitializer.createFileListView(
                                    ServiceDetailActivity.this, files, customerData.getUid(), getServiceType(), serviceId
                            )
                    );
                    customerData.removeListeners();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, databaseError.toString());
                }
            });
        } else {
            sendErrorMessage("Intent is Null");
        }
    }

    public void removeFiles(String customerId, List<FirebaseFile> files) {
        for(FirebaseFile file: files) {
            Log.d(TAG, "Deleting file:" + file.getFilePath(customerId));
            MarkdFirebaseStorage.deleteImage(file.getFilePath(customerId));
        }
    }
    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        onSupportNavigateUp();
    }
    @Override
    public boolean onSupportNavigateUp(){
        Log.i(TAG, "Navigate Up");
        hideKeyboard(this.getCurrentFocus());
        goBackToActivity(originalActivity);
        if(isAddService) {
            //delete service and files
            List<FirebaseFile> files = customerData.getServices(getServiceType()).get(serviceId).getFiles();
            removeFiles(customerData.getUid(), files);
            customerData.removeService(serviceId, getServiceType());
        } else {
            //Do nothing
        }
        return true;
    }
    private void goBackToActivity(Class<?> originalActivity) {
        Log.d(TAG, "isContractor:" + isContractorEditingPage);
        Intent originalActivityIntent = new Intent(getApplicationContext(), originalActivity);
        originalActivityIntent.putExtra("isContractor", isContractorEditingPage);
        if(customerData != null) {
            originalActivityIntent.putExtra("customerId", customerData.getUid());
        } else {
            Log.e(TAG, "customerData was null");
        }
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
