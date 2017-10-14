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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.data_objects.Boiler;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.HotWater;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.OnGetDataListener;
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

    private static final String TAG = "PlumbingActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;

    private HotWater hotWater;
    private Boiler boiler;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_plumbing_view);

        authentication = new FirebaseAuthentication(this);
        new ActionBarInitializer(this, true);

        plumbingContractor = findViewById(R.id.plumbing_footer);
        plumbingServiceList = findViewById(R.id.plumbing_service_list);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        customerData = new TempCustomerData((authentication.getCurrentUser().getUid()), new PlumbingGetDataListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
    }

    //MARK:- UI Initializers
    private void initializeUI() {
        initializeHotWater();
        initializeBoiler();
        initializeContractorServices();
        initializeFooter();
    }
    private void initializeHotWater() {
        hotWater = customerData.getHotWater();
        hotWaterEditButton = findViewById(R.id.plumbing_hot_water_edit);
        hotWaterEditButton.setOnClickListener(hotWaterEditButtonClickListener);

        if(hotWater == null) {
            return;
        }

        hotWaterManufacturerView = findViewById(R.id.plumbing_hot_water_manufacturer);
        hotWaterManufacturerView.setText(hotWater.getManufacturer());

        hotWaterModelView = findViewById(R.id.plumbing_hot_water_model);
        hotWaterModelView.setText(hotWater.getModel());

        hotWaterInstallDateView = findViewById(R.id.plumbing_hot_water_install_date);
        hotWaterInstallDateView.setText(hotWater.installDateAsString());

        hotWaterLifeSpanView = findViewById(R.id.plumbing_hot_water_life_span);
        hotWaterLifeSpanView.setText(hotWater.lifeSpanAsString());
    }
    private void initializeBoiler() {
        boiler = customerData.getBoiler();
        boilerEditButton = findViewById(R.id.plumbing_boiler_edit);
        boilerEditButton.setOnClickListener(boilerEditButtonClickListener);

        if(boiler == null) {
            return;
        }

        boilerManufacturerView = findViewById(R.id.plumbing_boiler_manufacturer);
        boilerManufacturerView.setText(boiler.getManufacturer());

        boilerModelView = findViewById(R.id.plumbing_boiler_model);
        boilerModelView.setText(boiler.getModel());

        boilerInstallDateView = findViewById(R.id.plumbing_boiler_install_date);
        boilerInstallDateView.setText(boiler.installDateAsString());

        boilerLifeSpanView = findViewById(R.id.plumbing_boiler_life_span);
        boilerLifeSpanView.setText(boiler.lifeSpanAsString());
    }
    private void initializeContractorServices() {
        View serviceListView = createServiceListView(PlumbingActivity.this, customerData.getPlumbingServices(), "SDR Plumbing & Heating Inc", "/services/plumbing");
        plumbingServiceList.addView(serviceListView);
    }
    private void initializeFooter() {
        ContractorDetails plumber = customerData.getPlumber();
        if(plumber != null) {
            Drawable logo = ContextCompat.getDrawable(PlumbingActivity.this, R.drawable.sdr_logo);
            View v = ContractorFooterViewInitializer.createFooterView(PlumbingActivity.this, logo, plumber.getCompanyName(), plumber.getTelephoneNumber(), plumber.getWebsiteUrl());
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
        intent.putExtra("lifespanInteger", hotWater.getLifeSpan());
        intent.putExtra("units", hotWater.getUnits());
        return intent;
    }
    private Intent putBoilerExtras(Intent intent) {
        intent.putExtra("manufacturer", boilerManufacturerView.getText());
        intent.putExtra("model", boilerModelView.getText());
        intent.putExtra("installDate", boilerInstallDateView.getText());
        intent.putExtra("lifespanInteger", boiler.getLifeSpan());
        intent.putExtra("units", boiler.getUnits());
        return intent;
    }

    private class PlumbingGetDataListener implements OnGetDataListener {
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
