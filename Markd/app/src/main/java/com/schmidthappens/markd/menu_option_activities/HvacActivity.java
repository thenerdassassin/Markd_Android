package com.schmidthappens.markd.menu_option_activities;

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
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.ViewInitializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.ViewInitializers.NavigationDrawerInitializer;
import com.schmidthappens.markd.ViewInitializers.ServiceListViewInitializer;
import com.schmidthappens.markd.data_objects.TempContractorServiceData;

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
    FrameLayout airHandlerServiceList;

    ImageView compressorEditButton;
    FrameLayout compressorServiceList;

    FrameLayout hvacContractor;

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
        airHandlerEditButton = (ImageView)findViewById(R.id.hvac_air_handler_edit);
        airHandlerServiceList = (FrameLayout)findViewById(R.id.hvac_air_handler_service_list);

        compressorEditButton = (ImageView)findViewById(R.id.hvac_compressor_edit);
        compressorServiceList = (FrameLayout)findViewById(R.id.hvac_compressor_service_list);

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

    private View.OnClickListener airHandlerEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("Click:", "Hot Water Edit");
            Toast.makeText(getApplicationContext(), "Air Handler Edit Clicked!", Toast.LENGTH_SHORT).show();
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
            Log.d("Click:", "Boiler Edit");
            Toast.makeText(getApplicationContext(), "Compressor Edit Clicked!", Toast.LENGTH_SHORT).show();
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
}
