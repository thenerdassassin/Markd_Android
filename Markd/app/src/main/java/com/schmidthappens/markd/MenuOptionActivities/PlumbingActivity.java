package com.schmidthappens.markd.MenuOptionActivities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.schmidthappens.markd.AdapterClasses.ServiceListAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.TempContractorServiceData;

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
    ListView hotWaterServiceList;
    ImageView hotWaterAddServiceButton;

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
        hotWaterEditButton = (ImageView)findViewById(R.id.hot_water_edit);
        hotWaterAddServiceButton = (ImageView)findViewById(R.id.add_hot_water_service_button);
        hotWaterServiceList = (ListView)findViewById(R.id.hot_water_service_list);

        //Set Up Buttons
        hotWaterEditButton.setOnClickListener(hotWaterEditButtonClickListener);
        hotWaterAddServiceButton.setOnClickListener(hotWaterAddServiceClickListener);

        //TODO change to http call to get service list
        final TempContractorServiceData serviceData = TempContractorServiceData.getInstance();
        ArrayAdapter hotWaterAdapter = new ServiceListAdapter(this, R.layout.service_list_row, serviceData.getHotWaterServices());
        hotWaterServiceList.setAdapter(hotWaterAdapter);
        hotWaterServiceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO move to detailed view
                Toast.makeText(getApplicationContext(), serviceData.getHotWaterServices().get(position).getComments(), Toast.LENGTH_LONG).show();
            }
        });
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
            Toast.makeText(getApplicationContext(), "Add New Service", Toast.LENGTH_SHORT).show();
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
