package com.schmidthappens.markd.customer_menu_activities;

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
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.schmidthappens.markd.AdapterClasses.PanelListAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.TempContractorServiceData;
import com.schmidthappens.markd.data_objects.TempPanelData;
import com.schmidthappens.markd.electrical_subactivities.PanelDetailActivity;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.schmidthappens.markd.view_initializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;

import static com.schmidthappens.markd.view_initializers.ServiceListViewInitializer.createServiceListView;

/**
 * Created by Josh on 3/24/2017.
 */


public class ElectricalActivity extends AppCompatActivity {
    //ActionBar
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    //XML Objects
    ListView panelList;
    TextView addPanelHyperlink;
    FrameLayout electricalServiceList;
    FrameLayout electricalContractor;

    ArrayAdapter adapter;
    TempPanelData panelData = TempPanelData.getInstance();
    private static final String TAG = "ElectricalActivity";

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_electrical_view);

        //TODO: remove session manager
        SessionManager sessionManager = new SessionManager(ElectricalActivity.this);
        sessionManager.checkLogin();
        new ActionBarInitializer(this, true, "customer"); //TODO: change to get customer from Firebase

        //Initialize XML Objects
        electricalContractor = (FrameLayout)findViewById(R.id.electrical_footer);
        electricalServiceList = (FrameLayout)findViewById(R.id.electrical_service_list);

        //Initialize Panel List
        //TODO change to get panels from TempCustomerData
        panelData = TempPanelData.getInstance();
        panelList = (ListView)findViewById(R.id.electrical_panel_list);
        View headerView = getLayoutInflater().inflate(R.layout.list_header_panel, panelList, false);
        panelList.addHeaderView(headerView);
        adapter = new PanelListAdapter(this, R.layout.list_row_panel, panelData.getPanels());
        panelList.setAdapter(adapter);

        //Set Up Add Panel Hyperlink
        addPanelHyperlink = (TextView)findViewById(R.id.electrical_add_panel_hyperlink);
        addPanelHyperlink.setOnClickListener(addPanelOnClickListener);

        //Set up ElectricalService List
        //TODO change to get electrical services/contractor from TempCustomerData
        TempContractorServiceData serviceData = TempContractorServiceData.getInstance();

        View electricalServiceListView = createServiceListView(this, serviceData.getElectricalServices(), "Conn-West Electric", false, "blue"); //TODO: change like plumbing page
        electricalServiceList.addView(electricalServiceListView);

        //Set up ElectricalContractor
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.connwestlogocrop);
        View v = ContractorFooterViewInitializer.createFooterView(this, logo, "Conn-West Electric", "203.922.2011", "connwestelectric.com");
        electricalContractor.addView(v);
    }

    //Mark:- OnClick Listeners
    private View.OnClickListener addPanelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "Add Panel Clicked");
            Context context = ElectricalActivity.this;
            Class destinationClass = PanelDetailActivity.class;
            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            intentToStartDetailActivity.putExtra("isMainPanel", true);
            intentToStartDetailActivity.putExtra("newPanel", true);
            startActivity(intentToStartDetailActivity);
        }
    };

    public void deletePanel(int position) {
        //TODO change to delete panel from TempCustomerData
        Log.i(TAG, "Delete Panel " + panelData.getPanel(position).getPanelDescription());
        panelData.deletePanel(position);
        adapter.clear();
        adapter.addAll(panelData.getPanels());
        panelList.setAdapter(adapter);
    }
}
