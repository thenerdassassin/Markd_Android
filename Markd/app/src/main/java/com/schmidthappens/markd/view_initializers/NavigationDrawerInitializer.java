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
import com.schmidthappens.markd.data_objects.MenuItem;
import com.schmidthappens.markd.menu_option_activities.ElectricalActivity;
import com.schmidthappens.markd.menu_option_activities.HvacActivity;
import com.schmidthappens.markd.menu_option_activities.LandscapingActivity;
import com.schmidthappens.markd.menu_option_activities.MainActivity;
import com.schmidthappens.markd.menu_option_activities.PaintingActivity;
import com.schmidthappens.markd.menu_option_activities.PlumbingActivity;

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
        Log.i(TAG, "Selected Item-" + menuOptions[position]);
        if(menuOptions[position].equals("Home")) {
            Class destinationClass = MainActivity.class;
            Intent intentToStartHomeActivity = new Intent(context, destinationClass);
            context.startActivity(intentToStartHomeActivity);
        } else if(menuOptions[position].equals("Plumbing")) {
            Class destinationClass = PlumbingActivity.class;
            Intent intentToStartPlumbingActivity = new Intent(context, destinationClass);
            context.startActivity(intentToStartPlumbingActivity);
        } else if(menuOptions[position].equals("HVAC")) {
            Class destinationClass = HvacActivity.class;
            Intent intentToStartHvacActivity = new Intent(context, destinationClass);
            context.startActivity(intentToStartHvacActivity);
        } else if(menuOptions[position].equals("Electrical")) {
            Class destinationClass = ElectricalActivity.class;
            Intent intentToStartElectricalActivity = new Intent(context, destinationClass);
            context.startActivity(intentToStartElectricalActivity);
        }  else if(menuOptions[position].equals("Landscaping")) {
            Class destinationClass = LandscapingActivity.class;
            Intent intentToStartLandscapingActivity = new Intent(context, destinationClass);
            context.startActivity(intentToStartLandscapingActivity);
        } else if(menuOptions[position].equals("Painting")) {
            Class destinationClass = PaintingActivity.class;
            Intent intentToStartPaintingActivity = new Intent(context, destinationClass);
            context.startActivity(intentToStartPaintingActivity);
        } else if(menuOptions[position].equals("Sign Out")) {
            SessionManager sessionManager = new SessionManager(context);
            sessionManager.logoutUser();
        }
        drawerLayout.closeDrawer(drawerList);
    }

}
