package com.schmidthappens.markd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.schmidthappens.markd.RecyclerViewClasses.PanelAdapter;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.Panel;
import com.schmidthappens.markd.data_objects.TempPanelData;

interface PanelSwipeHandler {
    void onSwipe(boolean isLeft);
}

public class MainActivity extends AppCompatActivity implements PanelSwipeHandler, PanelAdapter.PanelAdapterOnClickHandler {
    //Recycler View
    private SwipeableRecyclerView recList;
    private PanelAdapter panelAdapter;
    private TextView panelTitle;

    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private String[] menuOptions;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private TempPanelData myPanels;
    public int currentPanel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up ActionBar
        setUpActionBar();

        //Initialize DrawerList
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        setUpDrawerMenu();

        //Set RecyclerView Layout
        recList = (SwipeableRecyclerView)findViewById(R.id.recycler_view);
        setUpRecyclerView();

        //TODO: Change to http call for user in future
        myPanels = TempPanelData.getInstance();
        //Can probably remove in future
        currentPanel = myPanels.currentPanel;

        //Attach PanelAdapter to View
        panelAdapter = new PanelAdapter(myPanels.getPanel(currentPanel), this);
        recList.setAdapter(panelAdapter);

        //Attach Interface to recList
        recList.setPanelSwipeHandler(this);

        //Set Panel Title
        panelTitle = (TextView)findViewById(R.id.panel_title);
        panelTitle.setText(myPanels.getPanel(myPanels.currentPanel).getPanelTitle());

        //Attach onClick Listener to panelTitle
        panelTitle.setOnClickListener(panelTitleOnClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity != null) {
            if(intentThatStartedThisActivity.hasExtra("actionType")) {
                //Deleting a Breaker
                if(intentThatStartedThisActivity.getStringExtra("actionType").equals("Delete Breaker")) {
                    if(intentThatStartedThisActivity.hasExtra("breakerNumber")) {
                        int breakerToDelete = Integer.parseInt(intentThatStartedThisActivity.getStringExtra("breakerNumber"));
                        myPanels.updatePanel(myPanels.getPanel(myPanels.currentPanel).deleteBreaker(breakerToDelete));
                        panelAdapter.notifyDataSetChanged();
                    }
                }

            }
        }
    }

    /*//Create OptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_breaker:
                int nextBreakerInt = myPanels.getPanel(currentPanel).breakerCount()+1;
                Breaker newBreakerToAdd = new Breaker(nextBreakerInt, "");

                Context context = this;
                Class destinationClass = BreakerDetailActivity.class;
                Intent intentToStartDetailActivity = new Intent(context, destinationClass);
                intentToStartDetailActivity.putExtra("source", "MainActivity.addBreaker");
                passBreakerData(intentToStartDetailActivity, newBreakerToAdd);
                startActivity(intentToStartDetailActivity);

                return true;
            case R.id.action_add_panel:
                Log.d("Options Item", "Add Panel");
                //TODO action add panel
                return true;
            case R.id.edit_mode:
                Log.d("Options Item", "Edit MODE");
                //TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Mark:- Action Handlers
    @Override
    public void onClick(Breaker breakerClicked) {
        Context context = this;
        Class destinationClass = BreakerDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("source", "MainActivity.viewBreaker");
        passBreakerData(intentToStartDetailActivity, breakerClicked);
        startActivity(intentToStartDetailActivity);
    }

    public void onSwipe(boolean isLeftSwipe) {
        if(isLeftSwipe) {
            currentPanel = (currentPanel + 1) % myPanels.count();
        }
        else {
            if(currentPanel == 0)
                currentPanel = myPanels.count();
            currentPanel = currentPanel--;
        }
        Log.d("onSwipe-----", currentPanel + "");
        myPanels.currentPanel = currentPanel;
        panelAdapter.switchPanel(myPanels.getPanel(currentPanel));
        panelTitle.setText(myPanels.getPanel(currentPanel).getPanelTitle());
    }

    private View.OnClickListener panelTitleOnClickListener = new View.OnClickListener() {
        public void onClick(View view){
            Log.d("Clicked", "Panel Title Was Clicked");
            //Pass to PanelDetailActivity
            Context context = MainActivity.this;
            Class destinationClass = PanelDetailActivity.class;
            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            passPanelData(intentToStartDetailActivity, myPanels.getPanel(currentPanel));
            startActivity(intentToStartDetailActivity);
        }
    };

    // Mak:-- Helper functions
    private void passBreakerData(Intent intent, Breaker breaker) {
        intent.putExtra("breakerDescription", breaker.getBreakerDescription());
        intent.putExtra("breakerNumber", breaker.getNumber());
        intent.putExtra("breakerAmperage", breaker.getAmperage().toString());
        intent.putExtra("breakerType", breaker.getBreakerType().toString());
    }

    private void passPanelData(Intent intent, Panel panel) {
        intent.putExtra("isMainPanel", panel.getIsMainPanel());
        intent.putExtra("panelAmperage", panel.getAmperage().toString());
        intent.putExtra("manufacturer", panel.getManufacturer().toString());
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar);
        //Set up actionBarButtons
        ImageView menuButton = (ImageView)findViewById(R.id.burger_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
            }
        });
        ImageView editButton = (ImageView)findViewById(R.id.edit_mode);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Hit button action", "Edit Mode Activated");
            }
        });
    }

    //Mark:- DrawerMenu
    private void setUpDrawerMenu() {
        menuOptions = getResources().getStringArray(R.array.menu_options);
        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menuOptions));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        ImageView editButton = (ImageView)findViewById(R.id.edit_mode);
        editButton.setClickable(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        //TODO intent to new page
        drawerLayout.closeDrawer(drawerList);
    }
    // Mark:- RecyclerView
    private void setUpRecyclerView() {
        recList.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recList.setLayoutManager(gridLayoutManager);
    }

    public static class SwipeableRecyclerView extends RecyclerView {
        //Variables used to determine swipe
        private float x1, x2;
        static final int MIN_DISTANCE = 250;
        private PanelSwipeHandler panelswipeHandler;

        public SwipeableRecyclerView(Context context) {
            super(context);
        }

        public SwipeableRecyclerView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SwipeableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        // Mark:- Touch Event for Swiping
        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent){
            switch(motionEvent.getActionMasked())
            {
                case MotionEvent.ACTION_DOWN:
                    x1 = motionEvent.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = motionEvent.getX();
                    float deltaX = x2 - x1;
                    Log.d("DELTA----", deltaX + "");
                    if (Math.abs(deltaX) > MIN_DISTANCE)
                    {
                        if (panelswipeHandler != null) {
                            Log.d("Intercept", "SwipeLeft");
                            panelswipeHandler.onSwipe(true);
                        }
                        else if(panelswipeHandler != null) {
                            if (this.getAdapter() instanceof PanelAdapter) {
                                Log.d("Intercept", "SwipeRight");
                                panelswipeHandler.onSwipe(false);
                            }
                        }
                    }
                    break;
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        public void setPanelSwipeHandler(PanelSwipeHandler swipeHandler) {
            this.panelswipeHandler = swipeHandler;
        }
    }
}
