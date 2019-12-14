package com.schmidthappens.markd.customer_subactivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.schmidthappens.markd.AdapterClasses.EditApplianceRecyclerViewAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_menu_activities.HvacActivity;
import com.schmidthappens.markd.customer_menu_activities.PlumbingActivity;
import com.schmidthappens.markd.data_objects.AirHandler;
import com.schmidthappens.markd.data_objects.Boiler;
import com.schmidthappens.markd.data_objects.Compressor;
import com.schmidthappens.markd.data_objects.HotWater;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.StringUtilities;

/**
 * Created by Josh on 6/6/2017.
 */
public class ApplianceEditActivity extends AppCompatActivity {
    private static final String TAG = "ApplianceEditActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    private boolean isContractorEditingPage;
    private InputMethodManager IMM;

    public String manufacturer;
    public String model;
    public String installDate;
    public int lifespan;
    public String units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_appliance);
        IMM = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        authentication = new FirebaseAuthentication(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        customerData = new TempCustomerData(authentication, null);
        processIntent(getIntent());
        initializeXMLObjects();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(getCurrentFocus() != null) {
            Log.d(TAG, "Hiding Keyboard");
            hideKeyboard(getCurrentFocus());
        }
        saveAppliance();
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    //Mark:- Helper Functions
    private void initializeXMLObjects() {
        Log.d(TAG, "initializeXMLObjects");
        final RecyclerView recyclerView = findViewById(R.id.edit_appliance_recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(new EditApplianceRecyclerViewAdapter(this));
    }
    private void processIntent(Intent intent) {
        if(intent != null) {
            if(intent.hasExtra("title")) {
                setTitle(intent.getStringExtra("title"));
            }
            if(intent.hasExtra("manufacturer")) {
                manufacturer = intent.getStringExtra("manufacturer");
            }
            if(intent.hasExtra("model")) {
                model = intent.getStringExtra("model");
            }
            if(StringUtilities.isNullOrEmpty(intent.getStringExtra("installDate"))) {
                installDate = StringUtilities.getCurrentDateString();
            } else {
                installDate = intent.getStringExtra("installDate");
            }
            lifespan = intent.getIntExtra("lifespanInteger", 0);
            if(intent.hasExtra("units")) {
                units = intent.getStringExtra("units");
            }
            isContractorEditingPage = intent.getBooleanExtra("isContractor", false);
            if(isContractorEditingPage && intent.hasExtra("customerId")) {
                customerData = new TempCustomerData(intent.getStringExtra("customerId"), null);
            }
        }
    }
    public void hideKeyboard(View view) {
        IMM.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
    private void saveAppliance() {
        Class activityToGoTo = null;
        if(getTitle().equals("Domestic Hot Water")) {
            Log.i(TAG, "Save Hot Water Changes");
            customerData.updateHotWater(
                new HotWater(manufacturer, model, installDate, lifespan, units)
            );
            activityToGoTo = PlumbingActivity.class;
        } else if(getTitle().equals("Boiler")) {
            Log.i(TAG, "Save Boiler Changes");
            customerData.updateBoiler(
                new Boiler(manufacturer, model, installDate, lifespan, units)
            );
            activityToGoTo = PlumbingActivity.class;
        } else if(getTitle().equals("Compressor")) {
            Log.i(TAG, "Save Compressor Changes");
            customerData.updateCompressor(
                new Compressor(manufacturer, model, installDate, lifespan, units)
            );
            activityToGoTo = HvacActivity.class;
        } else if(getTitle().equals("Air Handler")) {
            Log.i(TAG, "Save Air Handler Changes");
            customerData.updateAirHandler(
                new AirHandler(manufacturer, model, installDate, lifespan, units)
            );
            activityToGoTo = HvacActivity.class;
        }
        goBackToActivity(activityToGoTo);
    }
    private void goBackToActivity(Class destinationClass){
        Intent activityIntent = new Intent(this, destinationClass);
        if(isContractorEditingPage) {
            activityIntent.putExtra("isContractor", true);
            activityIntent.putExtra("customerId", customerData.getUid());
        }
        startActivity(activityIntent);
        finish();
    }
}
