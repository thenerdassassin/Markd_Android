package com.schmidthappens.markd.view_initializers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.menu_option_activities.MainActivity;

/**
 * Created by Josh on 8/5/2017.
 */

public class ServiceDetailActivity extends AppCompatActivity {
    //XML Objects
    EditText editContractor;
    EditText editServiceDescription;
    Button saveButton;

    String pathOfFiles;
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
            Log.e(TAG, "getSupportActionBar returned Null");
            Toast.makeText(getApplicationContext(), "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
            goBackToActivity(MainActivity.class);
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

            if(intent.hasExtra("contractor")) {
                editContractor.setText(intent.getStringExtra("contractor"));
            }

            if(intent.hasExtra("description")) {
                editServiceDescription.setText(intent.getStringExtra("description"));
            }
        } else{
            Log.e(TAG, "Intent is Null");
            Toast.makeText(getApplicationContext(), "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
            goBackToActivity(MainActivity.class);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(ServiceDetailActivity.this.getCurrentFocus());
                //TODO save changes
                goBackToActivity(originalActivity);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        Log.i(TAG, "Navigate Up");
        goBackToActivity(originalActivity);
        finish();
        return true;
    }

    private void goBackToActivity(Class<?> originalActivity){
        Intent originalActivityIntent = new Intent(getApplicationContext(), originalActivity);
        startActivity(originalActivityIntent);
        finish();
    }

    private void initializeXMLObjects() {
        editContractor = (EditText)findViewById(R.id.service_edit_contractor);
        editServiceDescription = (EditText)findViewById(R.id.service_edit_description);
        saveButton = (Button)findViewById(R.id.service_edit_save_button);
    }

    //Hides Keyboard
    private void hideKeyboard(View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
