package com.schmidthappens.markd.customer_menu_activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.schmidthappens.markd.data_objects.AirHandler;
import com.schmidthappens.markd.data_objects.Compressor;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.schmidthappens.markd.view_initializers.ContractorFooterViewInitializer;

import static com.schmidthappens.markd.view_initializers.ServiceListViewInitializer.createServiceListView;

/**
 * Created by Josh on 4/18/2017.
 */

public class HvacActivity extends AppCompatActivity {
    //XML Objects
    ImageView airHandlerEditButton;
    TextView airHandlerManufacturerView;
    TextView airHandlerModelView;
    TextView airHandlerInstallDateView;
    TextView airHandlerLifeSpanView;

    ImageView compressorEditButton;
    TextView compressorManufacturerView;
    TextView compressorModelView;
    TextView compressorInstallDateView;
    TextView compressorLifeSpanView;

    FrameLayout hvacServiceList;
    FrameLayout hvacContractor;

    private static String TAG = "HvacActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    boolean isContractorViewingPage;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_hvac_view);
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
                customerData = new TempCustomerData(intentToProcess.getStringExtra("customerId"), new HVACGetDataListener());
            } else {
                Log.e(TAG, "No customer id");
                Toast.makeText(this, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            new ActionBarInitializer(this, true, "customer");
            customerData = new TempCustomerData(authentication, new HVACGetDataListener());
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

    // Mark: SetUp Function
    private void initializeAppliances() {
        initializeAirHandler();
        initializeCompressor();
    }
    private void initializeContractor(Contractor hvacTechnician) {
        initializeServices(hvacTechnician);
        initializeFooter(hvacTechnician);
    }
    private void initializeAirHandler() {
        AirHandler airHandler = customerData.getAirHandler();

        airHandlerEditButton = (ImageView)findViewById(R.id.hvac_air_handler_edit);
        airHandlerEditButton.setOnClickListener(airHandlerEditButtonClickListener);
        if(airHandler == null) {
            return;
        }
        airHandlerManufacturerView = (TextView)findViewById(R.id.hvac_air_handler_manufacturer);
        airHandlerManufacturerView.setText(airHandler.getManufacturer());

        airHandlerModelView = (TextView)findViewById(R.id.hvac_air_handler_model);
        airHandlerModelView.setText(airHandler.getModel());

        airHandlerInstallDateView = (TextView)findViewById(R.id.hvac_air_handler_install_date);
        airHandlerInstallDateView.setText(airHandler.installDateAsString());

        airHandlerLifeSpanView = (TextView)findViewById(R.id.hvac_air_handler_life_span);
        airHandlerLifeSpanView.setText(airHandler.lifeSpanAsString());
    }
    private void initializeCompressor() {
        Compressor compressor = customerData.getCompressor();

        compressorEditButton = (ImageView)findViewById(R.id.hvac_compressor_edit);
        compressorEditButton.setOnClickListener(compressorEditButtonClickListener);

        if(compressor == null) {
            return;
        }
        compressorManufacturerView = (TextView)findViewById(R.id.hvac_compressor_manufacturer);
        compressorManufacturerView.setText(compressor.getManufacturer());

        compressorModelView = (TextView)findViewById(R.id.hvac_compressor_model);
        compressorModelView.setText(compressor.getModel());

        compressorInstallDateView = (TextView)findViewById(R.id.hvac_compressor_install_date);
        compressorInstallDateView.setText(compressor.installDateAsString());

        compressorLifeSpanView = (TextView)findViewById(R.id.hvac_compressor_life_span);
        compressorLifeSpanView.setText(compressor.lifeSpanAsString());
    }
    private void initializeServices(Contractor hvacTechnician) {
        hvacServiceList = (FrameLayout)findViewById(R.id.hvac_service_list);
        String company;
        if(hvacTechnician != null && hvacTechnician.getContractorDetails() != null && hvacTechnician.getContractorDetails().getCompanyName() != null) {
            company = hvacTechnician.getContractorDetails().getCompanyName();
        } else {
            company = "";
        }

        View serviceListView = createServiceListView(this, customerData.getHvacServices(), company, isContractorViewingPage, customerData.getUid());
        hvacServiceList.addView(serviceListView);
    }
    private void initializeFooter(Contractor hvacTechnician) {
        hvacContractor = (FrameLayout)findViewById(R.id.hvac_footer);
        if(hvacTechnician == null || hvacTechnician.getContractorDetails() == null) {
            Log.d(TAG, "No plumber data");
            View v = ContractorFooterViewInitializer.createFooterView(HvacActivity.this);
            hvacContractor.addView(v);
        } else {
            ContractorDetails contractorDetails = hvacTechnician.getContractorDetails();
            Drawable logo = ContextCompat.getDrawable(HvacActivity.this, R.drawable.sdr_logo); //TODO: change to get logo
            View v = ContractorFooterViewInitializer.createFooterView(HvacActivity.this, logo, contractorDetails.getCompanyName(), contractorDetails.getTelephoneNumber(), contractorDetails.getWebsiteUrl());
            hvacContractor.addView(v);
        }
    }

    // Mark: OnClickListener
    private View.OnClickListener airHandlerEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Edit Air Handler");
            Class destinationClass = ApplianceEditActivity.class;
            Context context = HvacActivity.this;
            Intent intentToStartApplianceEditActivity = new Intent(context, destinationClass);
            intentToStartApplianceEditActivity = putExtras(intentToStartApplianceEditActivity, customerData.getAirHandler(), "Air Handler");
            startActivity(intentToStartApplianceEditActivity);
        }
    };
    private View.OnClickListener compressorEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Edit Compressor");
            Class destinationClass = ApplianceEditActivity.class;
            Context context = HvacActivity.this;
            Intent intentToStartApplianceEditActivity = new Intent(context, destinationClass);
            intentToStartApplianceEditActivity = putExtras(intentToStartApplianceEditActivity, customerData.getCompressor(), "Compressor");
            startActivity(intentToStartApplianceEditActivity);
        }
    };

    private Intent putExtras(Intent intent, AbstractAppliance appliance, String title) {
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

    private class HVACGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {
            Log.d(TAG, "Getting Hvac Data");
        }

        @Override
        public void onSuccess(DataSnapshot data) {
            Log.d(TAG, "Received Hvac Data");
            initializeAppliances();
            if(!customerData.getHvacTechnician(new HvacTechnicianGetDataListener())) {
                initializeContractor(null);
            }
        }

        @Override
        public void onFailed(DatabaseError databaseError) {
            Log.e(TAG, databaseError.toString());
            initializeContractor(null);
            Toast.makeText(HvacActivity.this, "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }
    private class HvacTechnicianGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {
            Log.d(TAG, "Getting HvacTechnician Data");
        }

        @Override
        public void onSuccess(DataSnapshot data) {
            Log.d(TAG, "Received HvacTechnician Data");
            initializeContractor(data.getValue(Contractor.class));
        }

        @Override
        public void onFailed(DatabaseError databaseError) {
            Log.e(TAG, databaseError.toString());
            initializeContractor(null);
            Toast.makeText(HvacActivity.this, "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }
}
