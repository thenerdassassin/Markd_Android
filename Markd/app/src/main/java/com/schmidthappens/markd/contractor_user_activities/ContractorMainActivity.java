package com.schmidthappens.markd.contractor_user_activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;

/**
 * Created by Josh on 9/16/2017.
 */

public class ContractorMainActivity extends AppCompatActivity {
    private final static String TAG = "ContractorMainActivity";

    //ActionBar
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contractor_main_view);

        SessionManager sessionManager = new SessionManager(ContractorMainActivity.this);
        sessionManager.checkLogin();

        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);

        //Set up ActionBar
        setUpActionBar();

        //Initialize DrawerList
        setUpDrawerToggle();
        NavigationDrawerInitializer ndi = new NavigationDrawerInitializer(this, drawerLayout, drawerList, drawerToggle, getResources().getStringArray(R.array.contractor_menu_options), getResources().getStringArray(R.array.contractor_menu_icons));
        ndi.setUp();

        //TODO
        Toast.makeText(this, "Contractor Type:" + sessionManager.getUserType(), Toast.LENGTH_LONG).show();
    }

    // Mark:- SetUp Functions
    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.view_action_bar);
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
