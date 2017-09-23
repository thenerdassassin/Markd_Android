package com.schmidthappens.markd.menu_option_activities;

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
import com.schmidthappens.markd.data_objects.Customer;
import com.schmidthappens.markd.data_objects.HotWater;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.view_initializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.TempContractorServiceData;
import com.schmidthappens.markd.data_objects.TempPlumbingData;
import com.schmidthappens.markd.plumbing_subactivities.PlumbingEditActivity;

import static com.schmidthappens.markd.view_initializers.ServiceListViewInitializer.createServiceListView;

/**
 * Created by Josh on 4/18/2017.
 */

public class PlumbingActivity extends AppCompatActivity {
    //ActionBar
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

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

    //TODO change to http call to get plumbing hot water/boiler data
    //TempPlumbingData plumbingData = TempPlumbingData.getInstance();
    HotWater hotWater = TempCustomerData.getInstance().getHotWater();
    Boiler boiler = TempCustomerData.getInstance().getBoiler();
    private static final String TAG = "PlumbingActivity";

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_plumbing_view);

        SessionManager sessionManager = new SessionManager(PlumbingActivity.this);
        sessionManager.checkLogin();

        //Initialize ActionBar
        setUpActionBar();

        //Initialize DrawerList
        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        setUpDrawerToggle();
        NavigationDrawerInitializer ndi = new NavigationDrawerInitializer(this, drawerLayout, drawerList, drawerToggle, getResources().getStringArray(R.array.menu_options), getResources().getStringArray(R.array.menu_icons));
        ndi.setUp();

        //Initialize XML Objects
        initializeHotWater();
        initializeBoiler();

        //Initialize Contractor Footer
        //TODO change to http call to get plumbing contractor
        plumbingContractor = (FrameLayout)findViewById(R.id.plumbing_footer);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.sdr_logo);
        View v = ContractorFooterViewInitializer.createFooterView(this, logo, "SDR Plumbing & Heating Inc", "203.348.2295", "sdrplumbing.com");
        plumbingContractor.addView(v);

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

    // Mark: SetUp Function
    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.view_action_bar);
        //Set up actionBarButtons
        ImageView menuButton = (ImageView) findViewById(R.id.burger_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
            }
        });
    }

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
        hotWaterLifeSpanView.setText(hotWater.getLifeSpan());
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
        boilerLifeSpanView.setText(boiler.getLifeSpan());
    }

    private View.OnClickListener hotWaterEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Edit Hot Water");
            Class destinationClass = PlumbingEditActivity.class;
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
            Class destinationClass = PlumbingEditActivity.class;
            Context context = PlumbingActivity.this;
            Intent intentToStartPlumbingEditActivity = new Intent(context, destinationClass);
            if(boiler != null) {
                intentToStartPlumbingEditActivity = putBoilerExtras(intentToStartPlumbingEditActivity);
            }
            intentToStartPlumbingEditActivity.putExtra("title", "Boiler");
            startActivity(intentToStartPlumbingEditActivity);
        }
    };

    private void setUpDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private Intent putHotWaterExtras(Intent intent) {
        intent.putExtra("manufacturer", hotWaterManufacturerView.getText());
        intent.putExtra("model", hotWaterModelView.getText());
        intent.putExtra("installDate", hotWaterInstallDateView.getText());
        intent.putExtra("lifespan", hotWaterLifeSpanView.getText());
        return intent;
    }

    private Intent putBoilerExtras(Intent intent) {
        intent.putExtra("manufacturer", boilerManufacturerView.getText());
        intent.putExtra("model", boilerModelView.getText());
        intent.putExtra("installDate", boilerInstallDateView.getText());
        intent.putExtra("lifespan", boilerLifeSpanView.getText());
        return intent;
    }
}
