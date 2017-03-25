package com.schmidthappens.markd.MenuOptionActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.schmidthappens.markd.AdapterClasses.PanelAdapter;
import com.schmidthappens.markd.BreakerDetailActivity;
import com.schmidthappens.markd.PanelDetailActivity;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.Panel;
import com.schmidthappens.markd.data_objects.TempPanelData;

interface PanelSwipeHandler {
    void onSwipe(boolean isLeft);
}
//TODO remove extra stuff from home page
public class ViewPanelActivity extends AppCompatActivity implements PanelAdapter.PanelAdapterOnClickHandler {
    //Recycler View
    private RecyclerView recList;
    private PanelAdapter panelAdapter;
    private TextView panelTitle;

    private TempPanelData myPanels;
    public int currentPanel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_panel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set RecyclerView Layout
        recList = (RecyclerView) findViewById(R.id.recycler_view);
        setUpRecyclerView();

        //TODO: Change to http call for user in future
        myPanels = TempPanelData.getInstance();
        //Can probably remove in future
        currentPanel = myPanels.currentPanel;

        //Attach PanelAdapter to View
        panelAdapter = new PanelAdapter(myPanels.getPanel(currentPanel), this);
        recList.setAdapter(panelAdapter);

        //Set Panel Title
        panelTitle = (TextView)findViewById(R.id.panel_title);
        panelTitle.setText(myPanels.getPanel(myPanels.currentPanel).getPanelTitle());

        //Attach onClick Listener to panelTitle
        panelTitle.setOnClickListener(panelTitleOnClickListener);
        setTitle(myPanels.getPanel(myPanels.currentPanel).getPanelDescription());
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

    @Override
    public boolean onSupportNavigateUp(){
        Context context = ViewPanelActivity.this;
        Class destinationClass = ElectricalActivity.class;
        Intent intentToStartElectricalActivity = new Intent(context, destinationClass);
        startActivity(intentToStartElectricalActivity);
        return true;
    }

    //Create OptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_panel_menu, menu);
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

    private View.OnClickListener panelTitleOnClickListener = new View.OnClickListener() {
        public void onClick(View view){
            Log.d("Clicked", "Panel Title Was Clicked");
            //Pass to PanelDetailActivity
            Context context = ViewPanelActivity.this;
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

    // Mark:- RecyclerView
    private void setUpRecyclerView() {
        recList.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recList.setLayoutManager(gridLayoutManager);
    };
}
