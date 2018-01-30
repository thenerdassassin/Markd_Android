package com.schmidthappens.markd.customer_subactivities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.schmidthappens.markd.utilities.ProgressBarUtilities;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 1/27/18.
 */

public class ServiceImageActivity extends AppCompatActivity {
    private final static String TAG = "ServiceImageActivity";
    FirebaseAuthentication authentication;
    TempCustomerData customerData;
    AlertDialog alertDialog;

    //XML Objects
    ProgressBar progressView;
    LinearLayout serviceFileEditForm;
    EditText editFileName;
    Button saveButton;
    Button deleteButton;

    boolean isNew;
    boolean isContractorEditingPage;
    String originalFileName;
    int serviceId;
    int fileId;
    Class<?> originalActivity;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.edit_view_service_image);
        authentication = new FirebaseAuthentication(this);

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
            sendErrorMessage("Not logged in");
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

    private void initializeXMLObjects() {
        serviceFileEditForm = (LinearLayout)findViewById(R.id.service_file_edit_form) ;
        progressView = (ProgressBar)findViewById(R.id.service_file_progress);
        editFileName = (EditText)findViewById(R.id.service_file_edit_name);
        setEnterButtonToKeyboardDismissal(editFileName);

        saveButton = (Button)findViewById(R.id.service_file_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(ServiceImageActivity.this.getCurrentFocus());
                saveFile();
            }
        });

        deleteButton = (Button)findViewById(R.id.service_file_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(ServiceImageActivity.this.getCurrentFocus());
                if(isNew) {
                    goBackToActivity(ServiceDetailActivity.class);
                } else {
                    showRemoveFileWarning();
                }
            }
        });
    }
    private void processIntent(Intent intent) {
        if(intent != null) {
            if(intent.hasExtra("originalActivity")) {
                originalActivity = (Class<?>)intent.getSerializableExtra("originalActivity");
            } else {
                sendErrorMessage("No originalActivity");
            }
            serviceId = intent.getIntExtra("serviceId", -1);
            fileId = intent.getIntExtra("fileId", -1);
            isContractorEditingPage = intent.getBooleanExtra("isContractor", false);
            isNew = intent.getBooleanExtra("isNew", false);
            if(isNew) {
                setTitle("Add File");
            } else {
                if(fileId == -1) {
                    sendErrorMessage("File Id not set");
                }
                setTitle("Edit File");
                if(intent.hasExtra("fileName")) {
                    originalFileName = intent.getStringExtra("fileName");
                    editFileName.setText(originalFileName);
                    editFileName.setSelection(originalFileName.length());
                }
            }
            if(intent.hasExtra("customerId")) {
                customerData = new TempCustomerData(intent.getStringExtra("customerId"), null);
            } else {
                sendErrorMessage("No customerId in intent");
            }
        }
    }

    private void saveFile() {
        //TODO: implement saveFile
        /*
        ProgressBarUtilities.showProgress(this, serviceFileEditForm, progressView, true);
                ProgressBarUtilities.showProgress(ServiceImageActivity.this, serviceFileEditForm, progressView, false);
                goBackToActivity(ServiceDetailActivity.class);
        */

        Intent backToServiceDetailIntent = new Intent(ServiceImageActivity.this, ServiceDetailActivity.class);
        backToServiceDetailIntent.putExtra("originalActivity", originalActivity);
        backToServiceDetailIntent.putExtra("isContractor", isContractorEditingPage);
        backToServiceDetailIntent.putExtra("customerId", customerData.getUid());
        backToServiceDetailIntent.putExtra("serviceId", serviceId);
        //Contractor
        //isNew

        if(isNew) {
            backToServiceDetailIntent.putExtra("newFile", editFileName.getText().toString());
        } else {
            backToServiceDetailIntent.putExtra("updateFileId", fileId);
            backToServiceDetailIntent.putExtra("updatedFile", editFileName.getText().toString());
        }
        startActivity(backToServiceDetailIntent);
    }
    private void showRemoveFileWarning() {
        //TODO: implement showRemoveFileWarning
    }

    @Override
    public boolean onSupportNavigateUp(){
        Log.i(TAG, "Navigate Up");
        hideKeyboard(this.getCurrentFocus());
        goBackToActivity(null);
        return true;
    }
    private void goBackToActivity(Class<?> originalActivity) {
        /*
        Log.d(TAG, "isContractor:" + isContractorEditingPage);
        Intent originalActivityIntent = new Intent(getApplicationContext(), originalActivity);
        originalActivityIntent.putExtra("isContractor", isContractorEditingPage);
        originalActivityIntent.putExtra("customerId", customerData.getUid());
        hideKeyboard(this.getCurrentFocus());
        startActivity(originalActivityIntent);
        */
        //TODO: test commented out with contractor logged in
        finish();
    }
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
            if(imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    private void sendErrorMessage(String message) {
        Log.e(TAG, message);
        Toast.makeText(getApplicationContext(), "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
        goBackToActivity(MainActivity.class);
    }
}
