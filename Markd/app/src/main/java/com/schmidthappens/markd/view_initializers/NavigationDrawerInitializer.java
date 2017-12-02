package com.schmidthappens.markd.view_initializers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.schmidthappens.markd.AdapterClasses.MenuDrawerListAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.contractor_user_activities.ContractorCustomersActivity;
import com.schmidthappens.markd.contractor_user_activities.ContractorMainActivity;
import com.schmidthappens.markd.contractor_user_activities.SendNotificationsActivity;
import com.schmidthappens.markd.customer_menu_activities.NotificationsActivity;
import com.schmidthappens.markd.customer_menu_activities.SettingsActivity;
import com.schmidthappens.markd.data_objects.MenuItem;
import com.schmidthappens.markd.customer_menu_activities.ElectricalActivity;
import com.schmidthappens.markd.customer_menu_activities.HvacActivity;
import com.schmidthappens.markd.customer_menu_activities.LandscapingActivity;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.customer_menu_activities.PaintingActivity;
import com.schmidthappens.markd.customer_menu_activities.PlumbingActivity;
import com.schmidthappens.markd.data_objects.TempCustomerData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 3/24/2017.
 */

public class NavigationDrawerInitializer {
    private static final String TAG = "NavigationDrawer";
    private Activity context;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private String[] menuOptions;
    private String[] menuIconStrings;
    private String userType;

    public NavigationDrawerInitializer(Activity context, String userType) {
        this.context = context;
        this.drawerLayout = context.findViewById(R.id.main_drawer_layout);
        this.drawerList = context.findViewById(R.id.left_drawer);
        this.userType = userType;
        setUp();
    }

    private void setUp() {
        setUpDrawerToggle();
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        Resources resources = context.getResources();
        if(userType == null) {
            new FirebaseAuthentication(context).signOut();
            return;
        } else if(userType.equals("customer")) {
            menuOptions = resources.getStringArray(R.array.menu_options);
            menuIconStrings = resources.getStringArray(R.array.menu_icons);
        } else {
            menuOptions = resources.getStringArray(R.array.contractor_menu_options);
            menuIconStrings = resources.getStringArray(R.array.contractor_menu_icons);
        }

        for(int i = 0; i < menuOptions.length; i++) {
            menuItemList.add(new MenuItem(menuIconStrings[i],menuOptions[i]));
        }

        drawerList.setAdapter(new MenuDrawerListAdapter(context, R.layout.list_item_drawer, menuItemList));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void setUpDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(context, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                (context).invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                (context).invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void selectItem(int position) {
        String selectedMenuItem = menuOptions[position];
        Log.i(TAG, "Selected Item-" + selectedMenuItem);

        Intent customerIntent;
        if(userType == null) {
            new FirebaseAuthentication(context).signOut();
            return;
        }

        if(userType.equals("customer")) {
            customerIntent = getCustomerIntent(selectedMenuItem);
        } else {
            customerIntent = getContractorIntent(selectedMenuItem);
        }

        if(customerIntent != null) {
            context.startActivity(customerIntent);
        } else {
            new FirebaseAuthentication(context).signOut();
        }

        drawerLayout.closeDrawer(drawerList);
    }

    private Intent getCustomerIntent(String selectedMenuItem) {
        Intent intentToReturn = null;
        switch (selectedMenuItem) {
            case "Home":
                intentToReturn = new Intent(context, MainActivity.class);
                break;
            case "Plumbing":
                intentToReturn = new Intent(context, PlumbingActivity.class);
                break;
            case"HVAC":
                intentToReturn = new Intent(context, HvacActivity.class);
                break;
            case "Electrical":
                intentToReturn = new Intent(context, ElectricalActivity.class);
                break;
            case"Landscaping":
                intentToReturn = new Intent(context, LandscapingActivity.class);
                break;
            case "Painting":
                intentToReturn = new Intent(context, PaintingActivity.class);
                break;
            case "Notifications":
                intentToReturn = new Intent(context, NotificationsActivity.class);
                break;
            case "Settings":
                intentToReturn = new Intent(context, SettingsActivity.class);
                break;
            default:
                Log.e(TAG, "Contractor selectedMenuItem not found-" + selectedMenuItem);
        }
        return intentToReturn;
    }
    private Intent getContractorIntent(String selectedMenuItem) {
        Intent intentToReturn = null;
        switch (selectedMenuItem) {
            case "Home":
                intentToReturn = new Intent(context, ContractorMainActivity.class);
                break;
            case "Customers":
                intentToReturn = new Intent(context, ContractorCustomersActivity.class);
                break;
            case"Settings":
                intentToReturn = new Intent(context, ContractorMainActivity.class);
                break;
            default:
                Log.e(TAG, "Contractor selectedMenuItem not found-" + selectedMenuItem);
        }
        return intentToReturn;
    }
}
