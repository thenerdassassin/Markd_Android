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

import com.schmidthappens.markd.AdapterClasses.PaintListAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.ViewInitializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.ViewInitializers.NavigationDrawerInitializer;
import com.schmidthappens.markd.data_objects.TempPaintData;

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


    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.painting_view);

        //Initialize ActionBar
        setUpActionBar();

        //Initialize DrawerList
        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        setUpDrawerToggle();
        NavigationDrawerInitializer ndi = new NavigationDrawerInitializer(this, drawerLayout, drawerList, drawerToggle);
        ndi.setUp();

        //TODO change to http call for paint
        final TempPaintData paintData = TempPaintData.getInstance();

        //Initialize Exterior Add Button
        addExteriorPaintButton = (ImageView)findViewById(R.id.painting_exterior_add_button);
        //addExteriorPaintButton.setOnClickListener(addPaintOnClickListener);

        //Set Up Exterior PaintList
        exteriorPaintList = (ListView)findViewById(R.id.painting_exterior_paint_list);
        ArrayAdapter exteriorAdapter = new PaintListAdapter(this, R.layout.paint_list_row, paintData.getExteriorPaints());
        exteriorPaintList.setAdapter(exteriorAdapter);
        exteriorPaintList.setOnItemClickListener(roomClickListener);

        //Initialize Interior Add Button
        addInteriorPaintButton = (ImageView)findViewById(R.id.painting_interior_add_button);
        addInteriorPaintButton.setOnClickListener(addPaintOnClickListener);

        //Set Up Interior PaintList
        interiorPaintList = (ListView)findViewById(R.id.painting_interior_paint_list);
        ArrayAdapter interiorAdapter = new PaintListAdapter(this, R.layout.paint_list_row, paintData.getInteriorPaints());
        interiorPaintList.setAdapter(interiorAdapter);
        interiorPaintList.setOnItemClickListener(roomClickListener);

        //Initialize Contractor Footer
        //TODO change to http call to get painting contractor
        paintingContractor = (FrameLayout)findViewById(R.id.painting_footer);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.mdf_logo);
        View v = ContractorFooterViewInitializer.createFooterView(this, logo, "MDF Painting & Power Washing", "203.348.2295", "mdfpainting.com");
        paintingContractor.addView(v);
    }

    // Mark: OnClickListeners
    private View.OnClickListener addPaintOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO set onItemClick to add room
            Log.d("Click:", "Add Room");
            Toast.makeText(getApplicationContext(), "Add Room to Paint Page!", Toast.LENGTH_SHORT).show();
        }
    };

    private AdapterView.OnItemClickListener roomClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO set onItemClick to edit paint
            Log.d("Click:", "View Room");
            Toast.makeText(getApplicationContext(), "Room Clicked", Toast.LENGTH_SHORT).show();
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
