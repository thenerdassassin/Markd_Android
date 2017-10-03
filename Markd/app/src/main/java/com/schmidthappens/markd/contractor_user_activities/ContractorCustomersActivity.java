package com.schmidthappens.markd.contractor_user_activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.schmidthappens.markd.AdapterClasses.CustomerListRecyclerViewAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.TempContractorData;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;

/**
 * Created by joshua.schmidtibm.com on 9/30/17.
 */

public class ContractorCustomersActivity extends AppCompatActivity {
    private final static String TAG = "ContractorCustomersActivity";

    //ActionBar
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private RecyclerView customerRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contractor_customers_view);

        SessionManager sessionManager = new SessionManager(ContractorCustomersActivity.this);
        sessionManager.checkLogin();

        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        customerRecyclerView = (RecyclerView)findViewById(R.id.contractor_customers_recycler_view);

        setUpRecyclerView();
        setUpActionBar();
        //Initialize DrawerList
        setUpDrawerToggle();
        NavigationDrawerInitializer ndi = new NavigationDrawerInitializer(this, drawerLayout, drawerList, drawerToggle, getResources().getStringArray(R.array.contractor_menu_options), getResources().getStringArray(R.array.contractor_menu_icons));
        ndi.setUp();
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        customerRecyclerView.setLayoutManager(layoutManager);
        customerRecyclerView.setHasFixedSize(true);
        customerRecyclerView.setAdapter(new CustomerListRecyclerViewAdapter(ContractorCustomersActivity.this, TempContractorData.getInstance().getCustomers()));
        customerRecyclerView.addItemDecoration(new DividerItemDecoration(ContractorCustomersActivity.this, DividerItemDecoration.VERTICAL));
    }
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
                Toast.makeText(getApplicationContext(), "" + customerRecyclerView.getAdapter().getItemCount(), Toast.LENGTH_SHORT).show();
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
