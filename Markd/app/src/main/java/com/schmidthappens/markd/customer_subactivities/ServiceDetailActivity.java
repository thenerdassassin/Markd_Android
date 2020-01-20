package com.schmidthappens.markd.customer_subactivities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_menu_activities.ServiceHistoryActivity;
import com.schmidthappens.markd.data_objects.ContractorService;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.file_storage.FirebaseFile;
import com.schmidthappens.markd.utilities.DateUtitilities;
import com.schmidthappens.markd.utilities.StringUtilities;
import com.schmidthappens.markd.view_initializers.ServiceFileListViewInitializer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    TextView editServiceDate;
    EditText editServiceDescription;
    FrameLayout fileList;
    Button saveButton;
    Button deleteButton;

    boolean isContractorEditingPage;
    String serviceType;
    int serviceId;
    boolean isNewService;

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
            final Intent intent = new Intent(this, LoginActivity.class);
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
        final String dateString = editServiceDate.getText().toString();
        if(serviceId < 0) {
            // New Service
            final ContractorService service = new ContractorService(
                    StringUtilities.getMonthFromDotFormattedString(dateString),
                    StringUtilities.getDayFromDotFormmattedString(dateString),
                    StringUtilities.getYearFromDotFormmattedString(dateString),
                    editContractor.getText().toString(),
                    editServiceDescription.getText().toString(),
                    null);
            addService(service);
        } else {
            updateService(
                    serviceId,
                    editContractor.getText().toString(),
                    dateString,
                    editServiceDescription.getText().toString(),
                    customerData.getServices(serviceType).get(serviceId).getFiles()
            );
        }
    }
    private void addService(final ContractorService service) {
        if(serviceType == null) {
            sendErrorMessage("Activity does not match");
        } else {
            Log.d(TAG, "Add " + serviceType + "service");
            customerData.addService(service, serviceType);
        }
    }
    private void updateService(int serviceId, String contractor, String date, String description, List<FirebaseFile> files) {
        if(serviceType == null) {
            sendErrorMessage("Activity does not match");
        } else {
            Log.d(TAG, "Edit " + serviceType + "service-" + serviceId);
            customerData.updateService(serviceId, contractor, date, description, files, serviceType);
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
                        goBackToActivity();
                    }
                })
                .create();
        alertDialog.show();
    }
    private void removeService() {
        if(serviceType == null) {
            sendErrorMessage("Activity does not match");
        } else {
            Log.d(TAG, "Remove " + serviceType + "service-" + serviceId);
            customerData.removeService(serviceId, serviceType);
        }
    }

    private void initializeXMLObjects() {
        editContractor = findViewById(R.id.service_edit_contractor);
        setEnterButtonToKeyboardDismissal(editContractor);
        editServiceDescription = findViewById(R.id.service_edit_description);
        setEnterButtonToKeyboardDismissal(editServiceDescription);

        editServiceDate = findViewById(R.id.service_edit_date);
        editServiceDate.setOnClickListener(view -> {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");
        });
        
        //Used to show done button but still allow multiple lines
        editServiceDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editServiceDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);

        fileList = findViewById(R.id.service_file_list);

        saveButton = findViewById(R.id.service_edit_save_button);
        saveButton.setOnClickListener(v -> {
            hideKeyboard(ServiceDetailActivity.this.getCurrentFocus());
            saveServiceData();
            goBackToActivity();
        });

        deleteButton = findViewById(R.id.service_edit_delete_button);
        deleteButton.setOnClickListener(v -> {
                hideKeyboard(ServiceDetailActivity.this.getCurrentFocus());
                if(serviceId < 0) {
                    goBackToActivity();
                } else {
                    showRemoveServiceWarning();
                }
            });
    }
    private void processIntent(final Intent intent) {
        Log.d(TAG, "Processing intent");
        if(intent != null) {
            if(intent.hasExtra("serviceType")) {
                serviceType = intent.getStringExtra("serviceType");
            }

            isContractorEditingPage = intent.getBooleanExtra("isContractor", false);

            if(intent.hasExtra("contractor")) {
                editContractor.setText(intent.getStringExtra("contractor"));
                editContractor.setSelection(editContractor.getText().length());
            }

            if(intent.hasExtra("serviceDate")) {
                editServiceDate.setText(intent.getStringExtra("serviceDate"));
            } else {
                changeInstallDate(
                        DateUtitilities.getCurrentMonth()+1,
                        DateUtitilities.getCurrentDay(),
                        DateUtitilities.getCurrentYear());
            }

            serviceId = intent.getIntExtra("serviceId", -1);
            if(serviceId < 0) {
                isNewService = true;
            } else {
                //Editing Service
                if(intent.hasExtra("description")) {
                    editServiceDescription.setText(intent.getStringExtra("description"));
                }
            }

            customerData = new TempCustomerData(intent.getStringExtra("customerId"), null);
            customerData.attachListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    List<FirebaseFile> files = new ArrayList<>();
                    if(isNewService) {
                        //Create New Service if new to be able to store information
                        saveServiceData();
                        serviceId = 0;
                        intent.putExtra("serviceId", 0);
                    } else {
                        if(customerData.getServices(serviceType) != null) {
                            ContractorService service = customerData.getServices(serviceType).get(serviceId);
                            if(service != null) {
                                files = service.getFiles();
                            }
                        }

                    }
                    fileList.removeAllViews();
                    fileList.addView(
                            ServiceFileListViewInitializer.createFileListView(
                                    ServiceDetailActivity.this,
                                    files,
                                    customerData.getUid(),
                                    serviceType,
                                    serviceId));
                    customerData.removeListeners();
                }

                @Override
                public void onCancelled(@NonNull final DatabaseError databaseError) {
                    Log.e(TAG, databaseError.toString());
                }
            });
        } else {
            sendErrorMessage("Intent is Null");
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
        goBackToActivity();
        if(isNewService) {
            //delete service
            customerData.removeService(serviceId, serviceType);
        }
        return true;
    }
    private void goBackToActivity() {
        Log.d(TAG, "isContractor:" + isContractorEditingPage);
        Intent originalActivityIntent = new Intent(this, ServiceHistoryActivity.class);
        originalActivityIntent.putExtra("isContractor", isContractorEditingPage);
        if(customerData != null) {
            originalActivityIntent.putExtra("customerId", customerData.getUid());
            originalActivityIntent.putExtra("contractorType", serviceType);
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
            final InputMethodManager imm =
                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    private void sendErrorMessage(String message) {
        Log.e(TAG, message);
        Toast.makeText(
                getApplicationContext(),
                "Oops...something went wrong.",
                Toast.LENGTH_SHORT
        ).show();
        goBackToActivity();
    }

    private void changeInstallDate(int month, int day, int year) {
        editServiceDate.setText(StringUtilities.getDateString(month, day, year));
    }
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of DatePickerDialog and return it
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            pickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            return pickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            Calendar current = Calendar.getInstance();

            //Check to make sure date is not in future
            if (selected.getTime().after(current.getTime())) {
                Log.i(TAG, "Selected Invalid Date");
                Toast.makeText(getActivity().getApplicationContext(), "Install date must not be in future.", Toast.LENGTH_SHORT).show();
                return;
            }
            ((ServiceDetailActivity)getActivity())
                    .changeInstallDate(month + 1, dayOfMonth, year);
        }
    }
}
