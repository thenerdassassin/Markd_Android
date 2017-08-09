package com.schmidthappens.markd.view_initializers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.ContractorService;
import com.schmidthappens.markd.data_objects.TempContractorServiceData;
import com.schmidthappens.markd.menu_option_activities.MainActivity;

import java.util.List;

/**
 * Created by Josh on 8/5/2017.
 */

public class ServiceDetailActivity extends AppCompatActivity {
    //XML Objects
    EditText editContractor;
    EditText editServiceDescription;
    Button saveButton;

    String pathOfFiles;
    int serviceId;
    boolean isNew;
    String TAG = "ServiceDetailActivity";

    Class<?> originalActivity;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.edit_view_service);

        SessionManager sessionManager = new SessionManager(ServiceDetailActivity.this);
        sessionManager.checkLogin();

        setTitle("Edit Service");
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(NullPointerException e) {
            sendErrorMessage("getSupportActionBar returned Null");
        }

        initializeXMLObjects();

        Intent intent = getIntent();
        if(intent != null) {
            if(intent.hasExtra("originalActivity")) {
                originalActivity = (Class<?>)intent.getSerializableExtra("originalActivity");
            }

            if(intent.hasExtra("pathOfFiles")) {
                pathOfFiles = intent.getStringExtra("pathOfFiles");
            }

            //defaults to false if "isNew" does not exist
            isNew = intent.getBooleanExtra("isNew", false);

            if(intent.hasExtra("serviceId")) {
                serviceId = Integer.parseInt(intent.getStringExtra("serviceId"));
            } else if(!isNew) {
                sendErrorMessage("Service Id doesn't exist");
            }

            if(intent.hasExtra("contractor")) {
                editContractor.setText(intent.getStringExtra("contractor"));
            } else if(isNew) {

            }

            if(intent.hasExtra("description")) {
                editServiceDescription.setText(intent.getStringExtra("description"));
            }
        } else {
            sendErrorMessage("Intent is Null");
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(ServiceDetailActivity.this.getCurrentFocus());
                //TODO save changes
                saveServiceData();
                goBackToActivity(originalActivity);
            }
        });
    }

    private void saveServiceData() {
        List<ContractorService> services;

        if(pathOfFiles.contains("electrical")) {
            services = TempContractorServiceData.getInstance().getElectricalServices();
        } else if(pathOfFiles.contains("plumbing")) {
            services = TempContractorServiceData.getInstance().getPlumbingServices();
        } else if(pathOfFiles.contains("hvac")) {
            services = TempContractorServiceData.getInstance().getHvacServices();
        } else {
            sendErrorMessage("Service pathOfFiles didn't match");
            return;
        }

        if(isNew) {
            //TODO change to get date
            services.add(new ContractorService(8, 8, 17, editContractor.getText().toString(), editServiceDescription.getText().toString()));
        } else {
            services.get(serviceId).update(editContractor.getText().toString(), editServiceDescription.getText().toString());
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
        hideKeyboard(this.getCurrentFocus());
        startActivity(originalActivityIntent);
        finish();
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
