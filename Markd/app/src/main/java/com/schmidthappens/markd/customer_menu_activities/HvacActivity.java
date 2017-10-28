package com.schmidthappens.markd.customer_menu_activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.AbstractAppliance;
import com.schmidthappens.markd.data_objects.AirHandler;
import com.schmidthappens.markd.data_objects.Compressor;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.TempContractorServiceData;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.customer_subactivities.ApplianceEditActivity;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.schmidthappens.markd.view_initializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;

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

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_hvac_view);

        authentication = new FirebaseAuthentication(this);
        if(getIntent().hasExtra("isContractor")) {
            new ActionBarInitializer(this, true, "contractor");
        } else {
            new ActionBarInitializer(this, true, "customer");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        customerData = new TempCustomerData(authentication, new HVACGetDataListener());
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
    }

    // Mark: SetUp Function
    private void initializeUI() {
        initializeAirHandler();
        initializeCompressor();
        initializeServices();
        initializeFooter();
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
    private void initializeServices() {
        hvacServiceList = (FrameLayout)findViewById(R.id.hvac_service_list);
        View serviceListView = createServiceListView(this, customerData.getHvacServices(), "AireServ", "/services/hvac");
        hvacServiceList.addView(serviceListView);
    }
    private void initializeFooter() {
        hvacContractor = (FrameLayout)findViewById(R.id.hvac_footer);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.aire_logo);
        ContractorDetails hvacTechnician = customerData.getHvacTechnician();
        if(hvacTechnician == null) {
            View v = ContractorFooterViewInitializer.createFooterView(this);
            hvacContractor.addView(v);
        } else {
            View v = ContractorFooterViewInitializer.createFooterView(this, logo, hvacTechnician.getCompanyName(), hvacTechnician.getTelephoneNumber(), hvacTechnician.getWebsiteUrl());
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
        }
        return intent;
    }

    private class HVACGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(DataSnapshot data) {
            initializeUI();
        }

        @Override
        public void onFailed(DatabaseError databaseError) {

        }
    }
}
