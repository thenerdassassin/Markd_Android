package com.schmidthappens.markd.customer_menu_activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_subactivities.ApplianceEditActivity;
import com.schmidthappens.markd.data_objects.AbstractAppliance;
import com.schmidthappens.markd.data_objects.Boiler;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.HotWater;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.schmidthappens.markd.view_initializers.ContractorFooterViewInitializer;

import static com.schmidthappens.markd.view_initializers.ServiceListViewInitializer.createServiceListView;

/**
 * Created by Josh on 4/18/2017.
 */

public class PlumbingActivity extends AppCompatActivity {
    private static final String TAG = "PlumbingActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    private boolean isContractorViewingPage;

    //XML Objects
    ImageView hotWaterEditButton;
    TextView hotWaterManufacturerView;
    TextView hotWaterModelView;
    TextView hotWaterInstallDateView;
    TextView hotWaterLifeSpanView;

    ImageView boilerEditButton;
    TextView boilerManufacturerView;
    TextView boilerModelView;
    TextView boilerInstallDateView;
    TextView boilerLifeSpanView;

    FrameLayout plumbingServiceList;
    FrameLayout plumbingContractor;

    private HotWater hotWater;
    private Boiler boiler;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_plumbing_view);
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

        Intent intentToProcess = getIntent();
        isContractorViewingPage = intentToProcess.getBooleanExtra("isContractor", false);
        if(isContractorViewingPage) {
            new ActionBarInitializer(this, true, "contractor");
            if(intentToProcess.hasExtra("customerId")) {
                customerData = new TempCustomerData(intentToProcess.getStringExtra("customerId"), new PlumbingGetDataListener());
            } else {
                Log.e(TAG, "No customer id");
                Toast.makeText(this, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            new ActionBarInitializer(this, true, "customer");
            customerData = new TempCustomerData((authentication.getCurrentUser().getUid()), new PlumbingGetDataListener());
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }

    //MARK:- UI Initializers
    private void initializeAppliances() {
        initializeHotWater();
        initializeBoiler();
    }
    private void initializeContractor(Contractor plumber, String plumberId) {
        initializeContractorServices(plumber);
        initializeFooter(plumber, plumberId);
    }
    private void initializeHotWater() {
        hotWater = customerData.getHotWater();
        hotWaterEditButton = (ImageView)findViewById(R.id.plumbing_hot_water_edit);
        hotWaterEditButton.setOnClickListener(hotWaterEditButtonClickListener);

        if(hotWater == null) {
            return;
        }

        hotWaterManufacturerView = (TextView)findViewById(R.id.plumbing_hot_water_manufacturer);
        hotWaterManufacturerView.setText(hotWater.getManufacturer());

        hotWaterModelView = (TextView)findViewById(R.id.plumbing_hot_water_model);
        hotWaterModelView.setText(hotWater.getModel());

        hotWaterInstallDateView = (TextView)findViewById(R.id.plumbing_hot_water_install_date);
        hotWaterInstallDateView.setText(hotWater.installDateAsString());

        hotWaterLifeSpanView = (TextView)findViewById(R.id.plumbing_hot_water_life_span);
        hotWaterLifeSpanView.setText(hotWater.lifeSpanAsString());
    }
    private void initializeBoiler() {
        boiler = customerData.getBoiler();
        boilerEditButton = (ImageView)findViewById(R.id.plumbing_boiler_edit);
        boilerEditButton.setOnClickListener(boilerEditButtonClickListener);

        if(boiler == null) {
            return;
        }

        boilerManufacturerView = (TextView)findViewById(R.id.plumbing_boiler_manufacturer);
        boilerManufacturerView.setText(boiler.getManufacturer());

        boilerModelView = (TextView)findViewById(R.id.plumbing_boiler_model);
        boilerModelView.setText(boiler.getModel());

        boilerInstallDateView = (TextView)findViewById(R.id.plumbing_boiler_install_date);
        boilerInstallDateView.setText(boiler.installDateAsString());

        boilerLifeSpanView = (TextView)findViewById(R.id.plumbing_boiler_life_span);
        boilerLifeSpanView.setText(boiler.lifeSpanAsString());
    }
    private void initializeContractorServices(Contractor plumber) {
        plumbingServiceList = (FrameLayout)findViewById(R.id.plumbing_service_list);
        String company;
        if(plumber != null && plumber.getContractorDetails() != null && plumber.getContractorDetails().getCompanyName() != null) {
            company = plumber.getContractorDetails().getCompanyName();
        } else {
            company = "";
        }

        View serviceListView = createServiceListView(PlumbingActivity.this, customerData.getPlumbingServices(), company, isContractorViewingPage, customerData.getUid());
        plumbingServiceList.addView(serviceListView);
    }
    private void initializeFooter(Contractor plumber, String plumberId) {
        plumbingContractor = (FrameLayout)findViewById(R.id.plumbing_footer);
        if(plumber == null || plumber.getContractorDetails() == null) {
            Log.d(TAG, "No plumber data");
            View v = ContractorFooterViewInitializer.createFooterView(PlumbingActivity.this, "Plumber");
            plumbingContractor.addView(v);
        } else {
            ContractorDetails contractorDetails = plumber.getContractorDetails();
            final String pathToLogoFile = "logos/" + plumberId + "/" + plumber.getLogoFileName();
            View v = ContractorFooterViewInitializer.createFooterView(
                    PlumbingActivity.this,
                    contractorDetails.getCompanyName(),
                    contractorDetails.getTelephoneNumber(),
                    contractorDetails.getWebsiteUrl(),
                    pathToLogoFile);
            plumbingContractor.addView(v);
        }
    }

    //MARK:- OnClickListeners
    private View.OnClickListener hotWaterEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Edit Hot Water");
            Class destinationClass = ApplianceEditActivity.class;
            Context context = PlumbingActivity.this;
            Intent intentToStartPlumbingEditActivity = new Intent(context, destinationClass);
            intentToStartPlumbingEditActivity = putIntentExtras(intentToStartPlumbingEditActivity, hotWater, "Domestic Hot Water");
            startActivity(intentToStartPlumbingEditActivity);
        }
    };

    private View.OnClickListener boilerEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Edit Boiler");
            Class destinationClass = ApplianceEditActivity.class;
            Context context = PlumbingActivity.this;
            Intent intentToStartPlumbingEditActivity = new Intent(context, destinationClass);
            intentToStartPlumbingEditActivity = putIntentExtras(intentToStartPlumbingEditActivity, boiler, "Boiler");
            startActivity(intentToStartPlumbingEditActivity);
        }
    };

    //MARK:- Intent Builders
    private Intent putIntentExtras(Intent intent, AbstractAppliance appliance, String title) {
        intent.putExtra("title", title);
        if(appliance != null) {
            intent.putExtra("manufacturer", appliance.getManufacturer());
            intent.putExtra("model", appliance.getModel());
            intent.putExtra("installDate", appliance.installDateAsString());
            intent.putExtra("lifespanInteger", appliance.getLifeSpan());
            intent.putExtra("units", appliance.getUnits());
            intent.putExtra("isContractor", isContractorViewingPage);
            intent.putExtra("customerId", customerData.getUid());
        }
        return intent;
    }

    private class PlumbingGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {
            Log.d(TAG, "Getting Plumbing Data");
        }

        @Override
        public void onSuccess(DataSnapshot data) {
            Log.d(TAG, "Received Plumbing Data");
            initializeAppliances();
            if(!customerData.getPlumber(new PlumberGetDataListener())) {
                initializeContractor(null, "");
            }
        }

        @Override
        public void onFailed(DatabaseError databaseError) {

        }
    }
    private class PlumberGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {
            Log.d(TAG, "Getting Plumber Data");
        }

        @Override
        public void onSuccess(DataSnapshot data) {
            Log.d(TAG, "Received Plumber Data");
            initializeContractor(data.getValue(Contractor.class), data.getKey());
        }

        @Override
        public void onFailed(DatabaseError databaseError) {
            Log.d(TAG, databaseError.toString());
            View v = ContractorFooterViewInitializer.createFooterView(PlumbingActivity.this, "Plumber");
            plumbingContractor.addView(v);
        }
    }
}
