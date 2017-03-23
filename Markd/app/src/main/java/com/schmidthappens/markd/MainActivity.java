package com.schmidthappens.markd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.schmidthappens.markd.RecyclerViewClasses.PanelAdapter;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.Panel;
import com.schmidthappens.markd.data_objects.TempPanelData;

interface PanelSwipeHandler {
    void onSwipe(boolean isLeft);
}

public class MainActivity extends AppCompatActivity implements PanelSwipeHandler, PanelAdapter.PanelAdapterOnClickHandler {

    private SwipeableRecyclerView recList;
    private PanelAdapter panelAdapter;
    private TextView panelTitle;

    private TempPanelData myPanels;
    public int currentPanel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recList = (SwipeableRecyclerView)findViewById(R.id.recycler_view);
        recList.setHasFixedSize(true);

        //Set RecyclerView Layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recList.setLayoutManager(gridLayoutManager);

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

    //Create OptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

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
