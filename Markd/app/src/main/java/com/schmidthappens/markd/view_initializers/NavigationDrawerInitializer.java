package com.schmidthappens.markd.view_initializers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.schmidthappens.markd.AdapterClasses.MenuDrawerListAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.contractor_user_activities.ContractorMainActivity;
import com.schmidthappens.markd.data_objects.MenuItem;
import com.schmidthappens.markd.customer_menu_activities.ElectricalActivity;
import com.schmidthappens.markd.customer_menu_activities.HvacActivity;
import com.schmidthappens.markd.customer_menu_activities.LandscapingActivity;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.customer_menu_activities.PaintingActivity;
import com.schmidthappens.markd.customer_menu_activities.PlumbingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 3/24/2017.
 */

public class NavigationDrawerInitializer {

    private Context context;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private String[] menuOptions; //= context.getResources().getStringArray(R.array.menu_options);
    private String[] menuIconStrings;// = context.getResources().getStringArray(R.array.menu_icons);
    private static final String TAG = "NavigationDrawer";

    public NavigationDrawerInitializer(Context context, DrawerLayout drawerLayout, ListView drawerList, ActionBarDrawerToggle drawerToggle, String[] menuOptions, String[] menuIconStrings){
        this.context = context;
        this.drawerLayout = drawerLayout;
        this.drawerList = drawerList;
        this.drawerToggle = drawerToggle;
        this.menuOptions = menuOptions;
        this.menuIconStrings = menuIconStrings;
    }

    public void setUp() {
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();

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

    private void selectItem(int position) {
        String selectedMenuItem = menuOptions[position];
        Log.i(TAG, "Selected Item-" + selectedMenuItem);
        SessionManager sessionManager = new SessionManager(context);
        String userType = sessionManager.getUserType();

        Intent customerIntent = null;
        if(userType == null) {
            sessionManager.logoutUser();
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
            sessionManager.logoutUser();
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
                intentToReturn = new Intent(context, ContractorMainActivity.class);
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
