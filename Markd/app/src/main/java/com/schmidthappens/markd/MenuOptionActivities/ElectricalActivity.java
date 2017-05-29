package com.schmidthappens.markd.MenuOptionActivities;

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
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.schmidthappens.markd.AdapterClasses.PanelListAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.ViewInitializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.ViewInitializers.NavigationDrawerInitializer;
import com.schmidthappens.markd.data_objects.TempContractorServiceData;
import com.schmidthappens.markd.data_objects.TempPanelData;

import static com.schmidthappens.markd.ViewInitializers.ServiceListViewInitializer.createServiceListView;

/**
 * Created by Josh on 3/24/2017.
 */


public class ElectricalActivity extends AppCompatActivity {
    //ActionBar
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    //XML Objects
    ListView panelList;
    FrameLayout electricalContractor;
    FrameLayout electricalServiceList;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.electrical_view);

        //Initialize XML Objects
        electricalContractor = (FrameLayout)findViewById(R.id.electrical_footer);
        electricalServiceList = (FrameLayout)findViewById(R.id.electrical_service_list);


        //Initialize ActionBar
        setUpActionBar();

        //Initialize DrawerList
        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        setUpDrawerToggle();
        NavigationDrawerInitializer ndi = new NavigationDrawerInitializer(this, drawerLayout, drawerList, drawerToggle);
        ndi.setUp();

        //TODO change to http call for panels
        final TempPanelData panelData = TempPanelData.getInstance();
        panelList = (ListView)findViewById(R.id.panel_list);
        View headerView = getLayoutInflater().inflate(R.layout.panel_list_header, panelList, false);
        panelList.addHeaderView(headerView);
        ArrayAdapter adapter = new PanelListAdapter(this, R.layout.panel_list_row, panelData.getPanels());
        panelList.setAdapter(adapter);
        panelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    Context context = ElectricalActivity.this;
                    Class destinationClass = ViewPanelActivity.class;
                    panelData.currentPanel = position-1;
                    Intent intentToStartViewPanelActivity = new Intent(context, destinationClass);
                    startActivity(intentToStartViewPanelActivity);
                }
            }
        });

        //Set up ElectricalService List
        //TODO change to http call for electrical services
        TempContractorServiceData serviceData = TempContractorServiceData.getInstance();

        View electricalServiceListView = createServiceListView(this, serviceData.getElectricalServices(), electricalOnClickListener);
        electricalServiceList.addView(electricalServiceListView);

        //Set up ElectricalContractor
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.connwestlogocrop);
        View v = ContractorFooterViewInitializer.createFooterView(this, logo, "Conn-West Electric", "203.922.2011", "connwestelectric.com");
        electricalContractor.addView(v);
    }

    // Mark:- SetUp Functions
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
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Hit button action", "Edit Mode Activated");
            }
        });
    }

    //Mark:- OnClick Listeners
    private View.OnClickListener electricalOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Add Electrical Service", Toast.LENGTH_SHORT).show();
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
