package com.schmidthappens.markd;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import com.schmidthappens.markd.RecyclerViewClasses.PanelAdapter;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.Panel;

import java.util.LinkedList;
import java.util.List;

interface PanelSwipeHandler {
    void onSwipe(boolean isLeft);
}

public class MainActivity extends AppCompatActivity implements PanelSwipeHandler, PanelAdapter.PanelAdapterOnClickHandler {

    private SwipeableRecyclerView recList;
    private PanelAdapter panelAdapter;

    private Panel[] panel;
    public int currentPanel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recList = (SwipeableRecyclerView) findViewById(R.id.recycler_view);
        recList.setHasFixedSize(true);

        //Set RecyclerView Layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recList.setLayoutManager(gridLayoutManager);

        //Will Be Removed in final implementation
        List<Breaker> breakerList = new LinkedList<Breaker>();
        breakerList.add(new Breaker(1, "Master Bedroom Receptacles"));
        breakerList.add(new Breaker(2, "Master Bedroom Lighting"));
        breakerList.add(new Breaker(3, "Master Bathroom GFCI"));
        breakerList.add(new Breaker(4, "Master Bathroom Floor Heat"));
        breakerList.add(new Breaker(5, "Bedroom Receptacles"));
        breakerList.add(new Breaker(6, "2nd Floor Hallway Lighting"));
        breakerList.add(new Breaker(7, "Washing Machine"));
        breakerList.add(new Breaker(8, "Dryer"));
        breakerList.add(new Breaker(9, "Hot water Heater"));
        breakerList.add(new Breaker(10, "Well pump"));
        breakerList.add(new Breaker(11, "Refrigerator"));
        breakerList.add(new Breaker(12, "Microwave"));
        breakerList.add(new Breaker(13, "Oven"));
        breakerList.add(new Breaker(14, "Kitchen Receptacles"));
        breakerList.add(new Breaker(15, "Kitchen Island Receptacles"));
        breakerList.add(new Breaker(16, "Kitchen Lighting"));
        breakerList.add(new Breaker(17, "Spot Lights"));
        breakerList.add(new Breaker(18, "Garbage Disposal"));
        breakerList.add(new Breaker(19, "Dishwasher"));
        breakerList.add(new Breaker(20, "Kitchen Hood"));
        breakerList.add(new Breaker(21, "Dining Room Receptacles"));
        breakerList.add(new Breaker(22, "Dining Room Lighting"));
        breakerList.add(new Breaker(23, "Living Room Receptacles"));
        breakerList.add(new Breaker(24, "Family Room Lighting"));
        breakerList.add(new Breaker(25, "Foyer Receptacles"));
        breakerList.add(new Breaker(26, "Foyer Lighting"));
        breakerList.add(new Breaker(27, "Furnace"));
        breakerList.add(new Breaker(28, "Air Compressor"));
        breakerList.add(new Breaker(29, "Air Handler"));
        breakerList.add(new Breaker(30, "Central Vacuum"));
        breakerList.add(new Breaker(31, "Sump Pump"));
        breakerList.add(new Breaker(32, "Basement Lighting"));
        breakerList.add(new Breaker(33, "Exterior Lighting"));
        breakerList.add(new Breaker(34, "Landscape Lighting"));
        breakerList.add(new Breaker(35, "Garage Door Receptacles"));

        List<Breaker> breakerList2 = new LinkedList<Breaker>();
        breakerList2.add(new Breaker(1, "Master Bedroom Receptacles"));
        breakerList2.add(new Breaker(2, "Master Bedroom Lighting"));
        breakerList2.add(new Breaker(3, "Master Bathroom GFCI"));
        breakerList2.add(new Breaker(4, "Master Bathroom Floor Heat"));
        breakerList2.add(new Breaker(5, "Bedroom Receptacles"));
        breakerList2.add(new Breaker(6, "2nd Floor Hallway Lighting"));
        breakerList2.add(new Breaker(7, "Washing Machine"));
        breakerList2.add(new Breaker(8, "Dryer"));
        breakerList2.add(new Breaker(9, "Hot water Heater"));
        breakerList2.add(new Breaker(10, "Well pump"));
        breakerList2.add(new Breaker(11, "Refrigerator"));
        breakerList2.add(new Breaker(12, "Microwave"));

        this.panel = new Panel[2];
        this.panel[0] = new Panel(breakerList);
        this.panel[1] = new Panel(breakerList2);

        //Attach PanelAdapter to View
        panelAdapter = new PanelAdapter(panel[currentPanel], this);
        recList.setAdapter(panelAdapter);
        //Attach Interface to recList
        recList.setPanelSwipeHandler(this);
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
                Log.d("Options Item", "Add Breaker");
                return true;
            case R.id.action_add_panel:
                Log.d("Options Item", "Add Panel");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(Breaker breakerClicked) {
        Context context = this;
        Class destinationClass = BreakerDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("breaker_desc", breakerClicked.getBreaker_description());
        startActivity(intentToStartDetailActivity);
    }

    public void onSwipe(boolean isLeftSwipe) {
        if(isLeftSwipe) {
            currentPanel = (currentPanel + 1) % panel.length;
        }
        else {
            if(currentPanel == 0)
                currentPanel = panel.length;
            currentPanel--;
        }
        Log.d("onSwipe-----", currentPanel + "");
        panelAdapter.switchPanel(panel[currentPanel]);
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
