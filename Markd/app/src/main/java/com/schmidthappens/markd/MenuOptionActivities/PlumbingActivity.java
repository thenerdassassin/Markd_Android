package com.schmidthappens.markd.MenuOptionActivities;

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
import android.view.Menu;
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

public class PlumbingActivity extends AppCompatActivity {
    //ActionBar
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    //XML Objects
    ImageView hotWaterEditButton;
    FrameLayout hotWaterServiceList;

    ImageView boilerEditButton;
    FrameLayout boilerServiceList;

    FrameLayout plumbingContractor;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.plumbing_view);

        //Initialize ActionBar
        setUpActionBar();

        //Initialize DrawerList
        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        setUpDrawerToggle();
        NavigationDrawerInitializer ndi = new NavigationDrawerInitializer(this, drawerLayout, drawerList, drawerToggle);
        ndi.setUp();

        //Initialize XML Objects
        hotWaterEditButton = (ImageView)findViewById(R.id.plumbing_hot_water_edit);
        hotWaterServiceList = (FrameLayout)findViewById(R.id.plumbing_hot_water_service_list);

        boilerEditButton = (ImageView)findViewById(R.id.plumbing_boiler_edit);
        boilerServiceList = (FrameLayout)findViewById(R.id.plumbing_boiler_service_list);

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
        View hotWaterServiceListView = createServiceListView(this, serviceData.getHotWaterServices(), hotWaterAddServiceClickListener);
        hotWaterServiceList.addView(hotWaterServiceListView);

        View boilerServiceListView = ServiceListViewInitializer.createServiceListView(this, serviceData.getBoilerServices(), boilerAddServiceClickListener);
        boilerServiceList.addView(boilerServiceListView);
    }

    // Mark: SetUp Function
    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar);
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
        ImageView editButton = (ImageView) findViewById(R.id.edit_mode);
        editButton.setVisibility(View.GONE);
    }

    private View.OnClickListener hotWaterEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("Click:", "Hot Water Edit");
            Toast.makeText(getApplicationContext(), "Hot Water Edit Clicked!", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener hotWaterAddServiceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Add New Hot Water Service", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener boilerEditButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("Click:", "Boiler Edit");
            Toast.makeText(getApplicationContext(), "Boiler Edit Clicked!", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener boilerAddServiceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), TempContractorServiceData.getInstance().getBoilerServices().size() + "", Toast.LENGTH_SHORT).show();
        }
    };


    // Mark:- DrawerMenu
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        ImageView editButton = (ImageView) findViewById(R.id.edit_mode);
        editButton.setClickable(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

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
