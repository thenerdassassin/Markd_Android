package com.schmidthappens.markd.MenuOptionActivities;

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
    ImageView addPaintButton;
    ListView paintList;
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

        //Initialize Add Button
        addPaintButton = (ImageView)findViewById(R.id.painting_add_button);
        addPaintButton.setOnClickListener(addPaintOnClickListener);

        //Set Up PaintList
        //TODO change to http call for paint
        final TempPaintData paintData = TempPaintData.getInstance();
        paintList = (ListView)findViewById(R.id.painting_paint_list);
        ArrayAdapter adapter = new PaintListAdapter(this, R.layout.paint_list_row, paintData.getPaints());
        paintList.setAdapter(adapter);
        paintList.setOnItemClickListener(roomClickListener);

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
        ImageView editButton = (ImageView) findViewById(R.id.edit_mode);
        editButton.setVisibility(View.GONE);
    }

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
