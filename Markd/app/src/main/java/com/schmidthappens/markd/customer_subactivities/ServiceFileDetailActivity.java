package com.schmidthappens.markd.customer_subactivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.data_objects.ContractorService;
import com.schmidthappens.markd.data_objects.TempCustomerData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 1/27/18.
 */

public class ServiceFileDetailActivity extends AppCompatActivity {
    private final static String TAG = "ServiceFileDetailActvty";
    FirebaseAuthentication authentication;
    TempCustomerData customerData;
    AlertDialog alertDialog;

    //XML Objects
    ProgressBar progressView;
    LinearLayout serviceFileEditForm;
    EditText editFileName;
    //TODO: Image
    Button saveButton;
    Button deleteButton;

    String serviceType;
    int serviceId;
    int fileId; // newFile == -1
    String originalFileName;

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
                hideKeyboard(ServiceFileDetailActivity.this.getCurrentFocus());
                saveFile();
            }
        });

        deleteButton = (Button)findViewById(R.id.service_file_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(ServiceFileDetailActivity.this.getCurrentFocus());
                if(fileId < 0) {
                    goBackToActivity();
                } else {
                    showRemoveFileWarning();
                }
            }
        });
    }
    private void processIntent(Intent intent) {
        if(intent != null) {
            if(intent.hasExtra("customerId")) {
                customerData = new TempCustomerData(intent.getStringExtra("customerId"), null);
            } else {
                sendErrorMessage("No customerId in intent");
            }
            serviceType = intent.getStringExtra("serviceType");
            serviceId = intent.getIntExtra("serviceId", -1);
            fileId = intent.getIntExtra("fileId", -1);
            if(fileId == -1) {
                setTitle("Add File");
            } else {
                setTitle("Edit File");
                if(intent.hasExtra("fileName")) {
                    originalFileName = intent.getStringExtra("fileName");
                    editFileName.setText(originalFileName);
                    editFileName.setSelection(originalFileName.length());
                }
            }
        }
    }

    private void saveFile() {
        //TODO: implement save file
        /*
        ProgressBarUtilities.showProgress(this, serviceFileEditForm, progressView, true);
                ProgressBarUtilities.showProgress(ServiceFileDetailActivity.this, serviceFileEditForm, progressView, false);
                goBackToActivity(ServiceDetailActivity.class);
        */

        ContractorService service = customerData.getServices(serviceType).get(serviceId);
        List<String> files = service.getFiles();
        if(fileId < 0) {
            if(files == null) {
                files = new ArrayList<>();
            }
            //New
            files.add(editFileName.getText().toString());
        } else {
            files.set(fileId, editFileName.getText().toString());
        }
        service.setFiles(files);
        customerData.updateService(serviceId, service.getContractor(), service.getComments(), files, serviceType);
        goBackToActivity();
    }
    private void showRemoveFileWarning() {
        //TODO: implement showRemoveFileWarning
    }

    @Override
    public boolean onSupportNavigateUp(){
        Log.i(TAG, "Navigate Up");
        goBackToActivity();
        return true;
    }
    private void goBackToActivity() {
        hideKeyboard(this.getCurrentFocus());
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
        goBackToActivity();
    }
}
