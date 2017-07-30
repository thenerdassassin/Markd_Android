package com.schmidthappens.markd.menu_option_activities;

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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.schmidthappens.markd.AdapterClasses.PaintListAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.ViewInitializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.ViewInitializers.NavigationDrawerInitializer;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.TempPaintData;
import com.schmidthappens.markd.painting_subactivities.PaintEditActivity;

/**
 * Created by Josh on 5/29/2017.
 */

public class PaintingActivity extends AppCompatActivity {
    //ActionBar
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    //XML Objects
    ImageView addExteriorPaintButton;
    ListView exteriorPaintList;

    ImageView addInteriorPaintButton;
    ListView interiorPaintList;
    private FrameLayout paintingContractor;

    //Adapters
    PaintListAdapter exteriorAdapter;
    PaintListAdapter interiorAdapter;
    //TODO change to http call for paint
    final TempPaintData paintData = TempPaintData.getInstance();
    private final static String TAG = "PaintingActivity";


    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_painting_view);

        SessionManager sessionManager = new SessionManager(PaintingActivity.this);
        sessionManager.checkLogin();

        //Initialize ActionBar
        setUpActionBar();

        //Initialize DrawerList
        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        setUpDrawerToggle();
        NavigationDrawerInitializer ndi = new NavigationDrawerInitializer(this, drawerLayout, drawerList, drawerToggle);
        ndi.setUp();

        //Initialize Exterior Add Button
        addExteriorPaintButton = (ImageView)findViewById(R.id.painting_exterior_add_button);
        addExteriorPaintButton.setOnClickListener(addExteriorPaintOnClickListener);

        //Set Up Exterior PaintList
        exteriorPaintList = (ListView)findViewById(R.id.painting_exterior_paint_list);
        exteriorAdapter = new PaintListAdapter(this, R.layout.paint_list_row, paintData.getExteriorPaints());
        exteriorAdapter.setIsExterior(true);
        exteriorPaintList.setAdapter(exteriorAdapter);

        //Initialize Interior Add Button
        addInteriorPaintButton = (ImageView)findViewById(R.id.painting_interior_add_button);
        addInteriorPaintButton.setOnClickListener(addInteriorPaintOnClickListener);

        //Set Up Interior PaintList
        interiorPaintList = (ListView)findViewById(R.id.painting_interior_paint_list);
        interiorAdapter = new PaintListAdapter(this, R.layout.paint_list_row, paintData.getInteriorPaints());
        interiorAdapter.setIsExterior(false);
        interiorPaintList.setAdapter(interiorAdapter);

        //Initialize Contractor Footer
        //TODO change to http call to get painting contractor
        paintingContractor = (FrameLayout)findViewById(R.id.painting_footer);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.mdf_logo);
        View v = ContractorFooterViewInitializer.createFooterView(this, logo, "MDF Painting & Power Washing", "203.348.2295", "mdfpainting.com");
        paintingContractor.addView(v);
    }

    // Mark: OnClickListeners
    private View.OnClickListener addExteriorPaintOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context activityContext = PaintingActivity.this;
            Class destinationClass = PaintEditActivity.class;

            Intent intentToStartPaintEditActivity = new Intent(activityContext, destinationClass);
            intentToStartPaintEditActivity.putExtra("isNew", true);
            intentToStartPaintEditActivity.putExtra("isExterior", true);
            activityContext.startActivity(intentToStartPaintEditActivity);
        }
    };

    private View.OnClickListener addInteriorPaintOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context activityContext = PaintingActivity.this;
            Class destinationClass = PaintEditActivity.class;

            //TODO set onItemClick to add room
            Intent intentToStartPaintEditActivity = new Intent(activityContext, destinationClass);
            intentToStartPaintEditActivity.putExtra("isNew", true);
            activityContext.startActivity(intentToStartPaintEditActivity);
        }
    };

    public void deletePaintObject(int position, boolean isExterior) {
        Log.i(TAG, "{Delete Paint Item:" + position + " isExterior:" + isExterior + "}");
        //TODO change to http calls to delete panel
        paintData.deletePaintObject(position, isExterior);

        //Used to reset the adapter
        if(isExterior) {
            exteriorPaintList.setAdapter(exteriorAdapter);
        } else {
            interiorPaintList.setAdapter(interiorAdapter);
        }
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
