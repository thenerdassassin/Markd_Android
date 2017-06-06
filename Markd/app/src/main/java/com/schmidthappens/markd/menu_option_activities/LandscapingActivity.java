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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.schmidthappens.markd.AdapterClasses.LandscapingHistoryAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.ViewInitializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.ViewInitializers.NavigationDrawerInitializer;
import com.schmidthappens.markd.data_objects.LandscapingSeason;
import com.schmidthappens.markd.data_objects.TempLandscapingData;

/**
 * Created by Josh on 6/1/2017.
 */

public class LandscapingActivity extends AppCompatActivity {
    //ActionBar
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    //XML Objects
    private ListView historyList;
    private FrameLayout landscapingContractor;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.landscaping_view);

        //Initialize ActionBar
        setUpActionBar();

        //Initialize DrawerList
        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        setUpDrawerToggle();
        NavigationDrawerInitializer ndi = new NavigationDrawerInitializer(this, drawerLayout, drawerList, drawerToggle);
        ndi.setUp();

        //Set Up PaintList
        //TODO change to http call for landscaping history
        final TempLandscapingData landscapingData = TempLandscapingData.getInstance();
        historyList = (ListView)findViewById(R.id.landscaping_history_list);
        ArrayAdapter adapter = new LandscapingHistoryAdapter(this, R.layout.landscaping_season_history_row, landscapingData.getHistory());
        historyList.setAdapter(adapter);
        historyList.setOnItemClickListener(seasonClickListener); //todo

        //Initialize Contractor Footer
        //TODO change to http call to get landscaping contractor
        landscapingContractor = (FrameLayout)findViewById(R.id.landscaping_footer);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.landscaping_logo);
        View v = ContractorFooterViewInitializer.createFooterView(this, logo, "Greenwich Landscape Company", "203.869.1022", "http://greenwichlandscape.net/");
        landscapingContractor.addView(v);

    }

    private AdapterView.OnItemClickListener seasonClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO set onItemClick to edit season
            Log.d("Click:", "View Season");
            LandscapingSeason s = (LandscapingSeason)parent.getItemAtPosition(position);
            Toast.makeText(getApplicationContext(), s.getComments(), Toast.LENGTH_LONG).show();
        }
    };

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
