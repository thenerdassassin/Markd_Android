package com.schmidthappens.markd.customer_subactivities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.schmidthappens.markd.AdapterClasses.EditHomeRecyclerViewAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.data_objects.TempCustomerData;

public class HomeEditActivity extends AppCompatActivity {
    private static final String TAG = "HomeEditActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    private InputMethodManager IMM;

    private Boolean isNewAccount;
    public String street;
    public String city;
    public String state;
    public String zipCode;
    public Double bedrooms;
    public Double bathrooms;
    public Integer squareFootage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IMM = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        setContentView(R.layout.edit_view_recycler);
        authentication = new FirebaseAuthentication(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        isNewAccount = !authentication.checkLogin();
        if(!isNewAccount) {
            customerData = new TempCustomerData(authentication, null);
            setTitle("Edit Home");
        } else {
            setUpActionBar();
        }

        processIntent(getIntent());
        initializeXMLObjects();
    }
    @Override
    public void onStop() {
        super.onStop();
        saveHome();
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }

    //Mark:- Set up functions
    private void setUpActionBar() {
        Log.d(TAG, "setUpActionBar");
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.view_action_bar);
        } else {
            Log.e(TAG, "ActionBar is null");
        }

        //Set up actionBarButtons
        final ImageView menuButton = findViewById(R.id.burger_menu);
        menuButton.setClickable(false);
        menuButton.setVisibility(View.GONE);
    }
    private void initializeXMLObjects() {
        Log.d(TAG, "initializeXMLObjects");
        final RecyclerView recyclerView = findViewById(R.id.edit_recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView
                .addItemDecoration(
                        new DividerItemDecoration(
                                HomeEditActivity.this,
                                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(
                new EditHomeRecyclerViewAdapter(this)
        );
    }
    private void processIntent(final Intent intentToProcess) {
        Log.d(TAG, "processIntent");
        if(intentToProcess != null && !isNewAccount) {
            if(intentToProcess.hasExtra("street")) {
                street = intentToProcess.getStringExtra("street");
            }
            if(intentToProcess.hasExtra("city")) {
                city = intentToProcess.getStringExtra("city");
            }
            if(intentToProcess.hasExtra("state")) {
                state = intentToProcess.getStringExtra("state");
            }
            if(intentToProcess.hasExtra("zipcode")) {
                zipCode = intentToProcess.getStringExtra("zipcode");
            }
            if(intentToProcess.hasExtra("bedrooms")) {
                String bedroomString = intentToProcess.getStringExtra("bedrooms");
                bedrooms = Double.valueOf(bedroomString);
            }
            if(intentToProcess.hasExtra("bathrooms")) {
                String bathroomString = intentToProcess.getStringExtra("bathrooms");
                bathrooms = Double.valueOf(bathroomString);
            }
            if(intentToProcess.hasExtra("squareFootage")) {
                String squareFootageString = intentToProcess.getStringExtra("squareFootage");
                squareFootage = Integer.valueOf(squareFootageString);
            }
        }
    }

    public void hideKeyboard(View view) {
        IMM.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public void update() {
        if(isValidHome()) {
            saveHome();
        }
    }

    private boolean isValidHome() {
        return !(street == null ||
                city == null ||
                state == null || state.length() != 2 ||
                zipCode == null || zipCode.length() != 5 ||
                bedrooms == null ||
                bathrooms == null ||
                squareFootage == null);
    }

    //Mark:- Helper functions
    private void saveHome() {
        customerData.updateHome(
                street,
                city,
                state,
                zipCode,
                bedrooms,
                bathrooms,
                squareFootage
        );
    }
}
