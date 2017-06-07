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
import com.schmidthappens.markd.ViewInitializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.ViewInitializers.NavigationDrawerInitializer;
import com.schmidthappens.markd.ViewInitializers.ServiceListViewInitializer;
import com.schmidthappens.markd.data_objects.TempContractorServiceData;
import com.schmidthappens.markd.data_objects.TempHvacData;
import com.schmidthappens.markd.hvac_subactivities.HvacEditActivity;

import static com.schmidthappens.markd.ViewInitializers.ServiceListViewInitializer.createServiceListView;

/**
 * Created by Josh on 4/18/2017.
 */

public class HvacActivity extends AppCompatActivity {

    //ActionBar
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    //XML Objects
    ImageView airHandlerEditButton;
    TextView airHandlerManufacturerView;
    TextView airHandlerModelView;
    TextView airHandlerInstallDateView;
    TextView airHandlerLifeSpanView;
    FrameLayout airHandlerServiceList;

    ImageView compressorEditButton;
    TextView compressorManufacturerView;
    TextView compressorModelView;
    TextView compressorInstallDateView;
    TextView compressorLifeSpanView;
    FrameLayout compressorServiceList;

    FrameLayout hvacContractor;

    private TempHvacData hvacData = TempHvacData.getInstance();
    private static String TAG = "HvacActivity";

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.hvac_view);

        //Initialize ActionBar
        setUpActionBar();

        //Initialize DrawerList
        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        setUpDrawerToggle();
        NavigationDrawerInitializer ndi = new NavigationDrawerInitializer(this, drawerLayout, drawerList, drawerToggle);
        ndi.setUp();

        //Initialize XML Objects
        initializeAirHandler();
        initializeCompressor();

        //Initialize Contractor Footer
        //TODO change to http call to get hvac contractor
        hvacContractor = (FrameLayout)findViewById(R.id.hvac_footer);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.aire_logo);
        View v = ContractorFooterViewInitializer.createFooterView(this, logo, "AireServ", "203.348.2295", "aireserv.com");
        hvacContractor.addView(v);

        //Set Up Buttons
        airHandlerEditButton.setOnClickListener(airHandlerEditButtonClickListener);
        compressorEditButton.setOnClickListener(compressorEditButtonClickListener);

        //TODO change to http call to get service lists
        final TempContractorServiceData serviceData = TempContractorServiceData.getInstance();

        //Set Up Service Lists
        View airHandlerServiceListView = createServiceListView(this, serviceData.getAirHandlerServices(), airHandlerAddServiceClickListener);
        airHandlerServiceList.addView(airHandlerServiceListView);

        View compressorServiceListView = ServiceListViewInitializer.createServiceListView(this, serviceData.getCompressorServices(), compressorAddServiceClickListener);
        compressorServiceList.addView(compressorServiceListView);
    }

    // Mark: SetUp Function
    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar);
        //Set up actionBarButtons
        ImageView menuButton = (ImageView)findViewById(R.id.burger_menu);
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

    private void initializeAirHandler() {
        airHandlerEditButton = (ImageView)findViewById(R.id.hvac_air_handler_edit);

        airHandlerManufacturerView = (TextView)findViewById(R.id.hvac_air_handler_manufacturer);
        airHandlerManufacturerView.setText(hvacData.getAirHandlerManufacturer());

        airHandlerModelView = (TextView)findViewById(R.id.hvac_air_handler_model);
        airHandlerModelView.setText(hvacData.getAirHandlerModel());

        airHandlerInstallDateView = (TextView)findViewById(R.id.hvac_air_handler_install_date);
        airHandlerInstallDateView.setText(hvacData.getAirHandlerInstallDate());

        airHandlerLifeSpanView = (TextView)findViewById(R.id.hvac_air_handler_life_span);
        airHandlerLifeSpanView.setText(hvacData.getAirHandlerLifeSpan());

        airHandlerServiceList = (FrameLayout)findViewById(R.id.hvac_air_handler_service_list);
    }

    private void initializeCompressor() {
        compressorEditButton = (ImageView)findViewById(R.id.hvac_compressor_edit);

        compressorManufacturerView = (TextView)findViewById(R.id.hvac_compressor_manufacturer);
        compressorManufacturerView.setText(hvacData.getCompressorManufacturer());

        compressorModelView = (TextView)findViewById(R.id.hvac_compressor_model);
        compressorModelView.setText(hvacData.getCompressorModel());

        compressorInstallDateView = (TextView)findViewById(R.id.hvac_compressor_install_date);
        compressorInstallDateView.setText(hvacData.getCompressorInstallDate());

        compressorLifeSpanView = (TextView)findViewById(R.id.hvac_compressor_life_span);
        compressorLifeSpanView.setText(hvacData.getCompressorLifeSpan());

        compressorServiceList = (FrameLayout)findViewById(R.id.hvac_compressor_service_list);
    }

    private View.OnClickListener airHandlerEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Edit Hot Water");
            Class destinationClass = HvacEditActivity.class;
            Context context = HvacActivity.this;
            Intent intentToStartHvacEditActivity = new Intent(context, destinationClass);
            intentToStartHvacEditActivity = putAirHandlerExtras(intentToStartHvacEditActivity);
            startActivity(intentToStartHvacEditActivity);
        }
    };

    private View.OnClickListener airHandlerAddServiceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Add New Air Handler Service", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener compressorEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Edit Boiler");
            Class destinationClass = HvacEditActivity.class;
            Context context = HvacActivity.this;
            Intent intentToStartHvacEditActivity = new Intent(context, destinationClass);
            intentToStartHvacEditActivity = putCompressorExtras(intentToStartHvacEditActivity);
            startActivity(intentToStartHvacEditActivity);
        }
    };

    private View.OnClickListener compressorAddServiceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Add New Compressor Service", Toast.LENGTH_SHORT).show();
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

    private Intent putAirHandlerExtras(Intent intent) {
        intent.putExtra("title", "Air Handler");
        intent.putExtra("manufacturer", airHandlerManufacturerView.getText());
        intent.putExtra("model", airHandlerModelView.getText());
        intent.putExtra("installDate", airHandlerInstallDateView.getText());
        intent.putExtra("lifespan", airHandlerLifeSpanView.getText());
        return intent;
    }

    private Intent putCompressorExtras(Intent intent) {
        intent.putExtra("title", "Compressor");
        intent.putExtra("manufacturer", compressorManufacturerView.getText());
        intent.putExtra("model", compressorModelView.getText());
        intent.putExtra("installDate", compressorInstallDateView.getText());
        intent.putExtra("lifespan", compressorLifeSpanView.getText());
        return intent;
    }
}
