package com.markd.applications.android.home.customer_menu_activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.markd.applications.android.home.AdapterClasses.PanelListAdapter;
import com.markd.applications.android.home.R;
import com.markd.applications.android.home.account_authentication.FirebaseAuthentication;
import com.markd.applications.android.home.account_authentication.LoginActivity;
import com.markd.applications.android.home.customer_subactivities.PanelDetailActivity;
import com.markd.applications.android.home.data_objects.Contractor;
import com.markd.applications.android.home.data_objects.ContractorDetails;
import com.markd.applications.android.home.data_objects.Panel;
import com.markd.applications.android.home.data_objects.TempCustomerData;
import com.markd.applications.android.home.utilities.OnGetDataListener;
import com.markd.applications.android.home.view_initializers.ActionBarInitializer;
import com.markd.applications.android.home.view_initializers.ContractorFooterViewInitializer;

import java.util.Collections;
import java.util.List;

/**
 * Created by Josh on 3/24/2017.
 */


public class ElectricalActivity extends AppCompatActivity {
    private static final String TAG = "ElectricalActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    private boolean isContractorViewingPage;

    //XML Objects
    ListView panelList;
    TextView addPanelHyperlink;
    FrameLayout electricalContractor;
    ArrayAdapter<Panel> adapter;

    AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_electrical_view);
        authentication = new FirebaseAuthentication(this);
        initializeXMLObjects();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        Intent intentToProcess = getIntent();
        isContractorViewingPage = intentToProcess.getBooleanExtra("isContractor", false);
        if(isContractorViewingPage) {
            Log.v(TAG, "contractor on page");
            new ActionBarInitializer(this, true, "contractor");
            if(intentToProcess.hasExtra("customerId")) {
                customerData = new TempCustomerData(intentToProcess.getStringExtra("customerId"), new ElectricalGetDataListener());
            } else {
                Log.e(TAG, "No customer id");
                Toast.makeText(this, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.v(TAG, "customer on page");
            new ActionBarInitializer(this, true, "customer");
            customerData = new TempCustomerData((authentication.getCurrentUser().getUid()), new ElectricalGetDataListener());
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
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
            intentToStartDetailActivity.putExtra("isContractor", isContractorViewingPage);
            intentToStartDetailActivity.putExtra("customerId", customerData.getUid());
            startActivity(intentToStartDetailActivity);
        }
    };

    private void initializeXMLObjects() {
        electricalContractor = (FrameLayout)findViewById(R.id.electrical_footer);
        panelList = (ListView)findViewById(R.id.electrical_panel_list);
        addPanelHyperlink = (TextView)findViewById(R.id.electrical_add_panel_hyperlink);
    }
    private void setUpPanelList() {
        if(panelList.getHeaderViewsCount() == 0) {
            View headerView = getLayoutInflater().inflate(R.layout.list_header_panel, panelList, false);
            panelList.addHeaderView(headerView);
        }
        List<Panel> panels;
        if(customerData.getPanels() == null) {
            panels = Collections.emptyList();
        } else {
            panels = customerData.getPanels();
        }
        if(adapter == null) {
            adapter = new PanelListAdapter(ElectricalActivity.this, R.layout.list_row_panel, panels, isContractorViewingPage, customerData.getUid());
        } else {
            adapter.clear();
            adapter.addAll(panels);
        }
        panelList.setAdapter(adapter);
        //Set Up Add Panel Hyperlink
        addPanelHyperlink.setOnClickListener(addPanelOnClickListener);
    }
    private void initializeFooter(Contractor electrician, String electricianId) {
        if(electrician == null || electrician.getContractorDetails() == null) {
            Log.d(TAG, "No electrician data");
            View v = ContractorFooterViewInitializer.createFooterView(ElectricalActivity.this, "Electrician");
            electricalContractor.addView(v);
        } else {
            ContractorDetails electricianDetails = electrician.getContractorDetails();
            final String pathToLogoFile = "logos/" + electricianId + "/" + electrician.getLogoFileName();
            View v = ContractorFooterViewInitializer.createFooterView(
                    this,
                    electricianDetails.getCompanyName(),
                    electricianDetails.getTelephoneNumber(),
                    electricianDetails.getWebsiteUrl(),
                    pathToLogoFile);
            electricalContractor.addView(v);
        }
    }

    private class ElectricalGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {
            Log.d(TAG, "Getting Electrical Data");
        }

        @Override
        public void onSuccess(DataSnapshot data) {
            Log.d(TAG, "Received Electrical Data");
            setUpPanelList();
            if(!customerData.getElectrician(new ElectricianGetDataListener())) {
                initializeFooter(null, "");
            }
        }

        @Override
        public void onFailed(DatabaseError databaseError) {

        }
    }
    private class ElectricianGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {
            Log.d(TAG, "Getting Electrician Data");
        }
        @Override
        public void onSuccess(DataSnapshot data) {
            Log.v(TAG, data.toString());
            Log.d(TAG, "Received Electrician Data");
            Contractor electrician = data.getValue(Contractor.class);
            initializeFooter(electrician, data.getKey());
        }
        @Override
        public void onFailed(DatabaseError databaseError) {
            Log.d(TAG, databaseError.toString());
            View v = ContractorFooterViewInitializer.createFooterView(ElectricalActivity.this, "Electrician");
            electricalContractor.addView(v);
        }
    }
}
