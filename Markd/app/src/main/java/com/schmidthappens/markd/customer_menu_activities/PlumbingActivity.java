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
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Boiler;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.HotWater;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.schmidthappens.markd.view_initializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.TempContractorServiceData;
import com.schmidthappens.markd.customer_subactivities.ApplianceEditActivity;

import static com.schmidthappens.markd.view_initializers.ServiceListViewInitializer.createServiceListView;

/**
 * Created by Josh on 4/18/2017.
 */

public class PlumbingActivity extends AppCompatActivity {
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

    HotWater hotWater = TempCustomerData.getInstance().getHotWater();
    Boiler boiler = TempCustomerData.getInstance().getBoiler();
    ContractorDetails plumber = TempCustomerData.getInstance().getPlumber();
    SessionManager sessionManager;
    private static final String TAG = "PlumbingActivity";

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_plumbing_view);

        sessionManager = new SessionManager(PlumbingActivity.this);
        sessionManager.checkLogin();
        new ActionBarInitializer(this, true);

        //Initialize XML Objects
        initializeHotWater();
        initializeBoiler();

        //Initialize Contractor Footer
        plumbingContractor = (FrameLayout)findViewById(R.id.plumbing_footer);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.sdr_logo);
        if(plumber != null) {
            View v = ContractorFooterViewInitializer.createFooterView(this, logo, plumber.getCompanyName(), plumber.getTelephoneNumber(), plumber.getWebsiteUrl());
            plumbingContractor.addView(v);
        }

        //Set Up Buttons
        hotWaterEditButton.setOnClickListener(hotWaterEditButtonClickListener);
        boilerEditButton.setOnClickListener(boilerEditButtonClickListener);

        //TODO change to http call to get service lists
        final TempContractorServiceData serviceData = TempContractorServiceData.getInstance();

        //Set Up Service Lists
        plumbingServiceList = (FrameLayout)findViewById(R.id.plumbing_service_list);
        View serviceListView = createServiceListView(this, serviceData.getPlumbingServices(), "SDR Plumbing & Heating Inc", "/services/plumbing");
        plumbingServiceList.addView(serviceListView);
    }

    //MARK:- XML Initializer
    private void initializeHotWater() {
        hotWaterEditButton = (ImageView)findViewById(R.id.plumbing_hot_water_edit);

        if(hotWater == null) {
            return;
        }

        hotWaterManufacturerView =(TextView)findViewById(R.id.plumbing_hot_water_manufacturer);
        hotWaterManufacturerView.setText(hotWater.getManufacturer());

        hotWaterModelView = (TextView)findViewById(R.id.plumbing_hot_water_model);
        hotWaterModelView.setText(hotWater.getModel());

        hotWaterInstallDateView = (TextView)findViewById(R.id.plumbing_hot_water_install_date);
        hotWaterInstallDateView.setText(hotWater.getInstallDate());

        hotWaterLifeSpanView = (TextView)findViewById(R.id.plumbing_hot_water_life_span);
        hotWaterLifeSpanView.setText(hotWater.getLifeSpanString());
    }
    private void initializeBoiler() {
        boilerEditButton = (ImageView)findViewById(R.id.plumbing_boiler_edit);

        if(boiler == null) {
            return;
        }

        boilerManufacturerView =(TextView)findViewById(R.id.plumbing_boiler_manufacturer);
        boilerManufacturerView.setText(boiler.getManufacturer());

        boilerModelView = (TextView)findViewById(R.id.plumbing_boiler_model);
        boilerModelView.setText(boiler.getModel());

        boilerInstallDateView = (TextView)findViewById(R.id.plumbing_boiler_install_date);
        boilerInstallDateView.setText(boiler.getInstallDate());

        boilerLifeSpanView = (TextView)findViewById(R.id.plumbing_boiler_life_span);
        boilerLifeSpanView.setText(boiler.getLifeSpanString());
    }

    //MARK:- OnClickListeners
    private View.OnClickListener hotWaterEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Edit Hot Water");
            Class destinationClass = ApplianceEditActivity.class;
            Context context = PlumbingActivity.this;
            Intent intentToStartPlumbingEditActivity = new Intent(context, destinationClass);
            if(hotWater != null) {
                intentToStartPlumbingEditActivity = putHotWaterExtras(intentToStartPlumbingEditActivity);
            }
            intentToStartPlumbingEditActivity.putExtra("title", "Domestic Hot Water");
            startActivity(intentToStartPlumbingEditActivity);
        }
    };

    private View.OnClickListener addServiceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Add New Plumbing Service", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener boilerEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Edit Boiler");
            Class destinationClass = ApplianceEditActivity.class;
            Context context = PlumbingActivity.this;
            Intent intentToStartPlumbingEditActivity = new Intent(context, destinationClass);
            if(boiler != null) {
                intentToStartPlumbingEditActivity = putBoilerExtras(intentToStartPlumbingEditActivity);
            }
            intentToStartPlumbingEditActivity.putExtra("title", "Boiler");
            startActivity(intentToStartPlumbingEditActivity);
        }
    };

    //MARK:- Intent Builders
    private Intent putHotWaterExtras(Intent intent) {
        intent.putExtra("manufacturer", hotWaterManufacturerView.getText());
        intent.putExtra("model", hotWaterModelView.getText());
        intent.putExtra("installDate", hotWaterInstallDateView.getText());
        intent.putExtra("lifespanInteger", hotWater.getLifeSpanInteger());
        intent.putExtra("units", hotWater.getUnits());
        return intent;
    }
    private Intent putBoilerExtras(Intent intent) {
        intent.putExtra("manufacturer", boilerManufacturerView.getText());
        intent.putExtra("model", boilerModelView.getText());
        intent.putExtra("installDate", boilerInstallDateView.getText());
        intent.putExtra("lifespanInteger", boiler.getLifeSpanInteger());
        intent.putExtra("units", boiler.getUnits());
        return intent;
    }
}