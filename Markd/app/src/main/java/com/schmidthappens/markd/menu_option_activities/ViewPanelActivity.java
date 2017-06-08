package com.schmidthappens.markd.menu_option_activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.schmidthappens.markd.AdapterClasses.PanelAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.Panel;
import com.schmidthappens.markd.data_objects.TempPanelData;
import com.schmidthappens.markd.electrical_subactivities.BreakerDetailActivity;
import com.schmidthappens.markd.electrical_subactivities.PanelDetailActivity;

//TODO remove extra stuff from home page
public class ViewPanelActivity extends AppCompatActivity implements PanelAdapter.PanelAdapterOnClickHandler {
    //Recycler View
    private RecyclerView recyclerList;
    private PanelAdapter panelAdapter;
    private TextView panelTitle;

    private TempPanelData myPanels;
    public int currentPanel = 0;

    private static final String TAG = "ViewPanelActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_panel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set RecyclerView Layout
        recyclerList = (RecyclerView) findViewById(R.id.recycler_view);
        setUpRecyclerView();

        //TODO: Change to http get panel list
        myPanels = TempPanelData.getInstance();
        //Can probably remove in future
        currentPanel = myPanels.currentPanel;

        //Attach PanelAdapter to View
        panelAdapter = new PanelAdapter(myPanels.getPanel(currentPanel), this);
        recyclerList.setAdapter(panelAdapter);

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
                        Log.i(TAG, "Delete Breaker " + breakerToDelete);
                        myPanels.updatePanel(myPanels.getPanel(myPanels.currentPanel).deleteBreaker(breakerToDelete));
                        panelAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Log.i(TAG, "Navigate Up");
        Context context = ViewPanelActivity.this;
        Class destinationClass = ElectricalActivity.class;
        Intent intentToStartElectricalActivity = new Intent(context, destinationClass);
        startActivity(intentToStartElectricalActivity);
        return true;
    }

    // Mark:- Action Handlers
    @Override
    public void onClick(Breaker breakerClicked) {
        Log.i(TAG, "Click Breaker " + breakerClicked.getNumber());
        Context context = this;
        Class destinationClass = BreakerDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("source", "MainActivity.viewBreaker");
        passBreakerData(intentToStartDetailActivity, breakerClicked);
        startActivity(intentToStartDetailActivity);
    }

    private View.OnClickListener panelTitleOnClickListener = new View.OnClickListener() {
        public void onClick(View view){
            Log.i(TAG, "Click Panel Title");
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
        intent.putExtra("panelDescription", panel.getPanelDescription());
        intent.putExtra("numberOfBreakers", panel.getBreakerList().size());
        intent.putExtra("isMainPanel", panel.getIsMainPanel());
        intent.putExtra("panelInstallDate", panel.getInstallDate());
        intent.putExtra("panelAmperage", panel.getAmperage().toString());
        intent.putExtra("manufacturer", panel.getManufacturer().toString());
    }

    // Mark:- RecyclerView
    private void setUpRecyclerView() {
        recyclerList.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerList.setLayoutManager(gridLayoutManager);
    };
}
