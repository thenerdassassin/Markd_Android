package com.schmidthappens.markd.customer_subactivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.schmidthappens.markd.AdapterClasses.PanelAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_menu_activities.ElectricalActivity;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.Customer;
import com.schmidthappens.markd.data_objects.Panel;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.OnGetDataListener;

public class ViewPanelActivity extends AppCompatActivity implements PanelAdapter.PanelAdapterOnClickHandler {
    private static final String TAG = "ViewPanelActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    private boolean isContractorViewingPage;

    //Recycler View
    private RecyclerView recyclerList;
    private PanelAdapter panelAdapter;
    private TextView panelTitle;

    private Panel selectedPanel;
    public int currentPanel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_panel);
        authentication = new FirebaseAuthentication(this);
        initializeXMLObjects();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        processIntent(getIntent());
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }

    private void initializeXMLObjects() {
        recyclerList = (RecyclerView) findViewById(R.id.recycler_view);
        setUpRecyclerView();
        panelTitle = (TextView)findViewById(R.id.panel_title);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if(getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            Log.e(TAG, "No action bar is not null");
        }
    }
    private void setUpRecyclerView() {
        recyclerList.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerList.setLayoutManager(gridLayoutManager);
    }
    private void processIntent(Intent intentThatStartedThisActivity) {
        if(intentThatStartedThisActivity != null) {
            Log.v(TAG, intentThatStartedThisActivity.toUri(0));
            if(intentThatStartedThisActivity.hasExtra("panelId")) {
                currentPanel = intentThatStartedThisActivity.getIntExtra("panelId", 0);
            } else {
                Log.e(TAG, "No panel id in intent");
                Toast.makeText(this, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
            }

            isContractorViewingPage = intentThatStartedThisActivity.getBooleanExtra("isContractor", false);
            Log.d(TAG, "processIntent ViewPanelActivity isContractor:"+isContractorViewingPage);
            if(intentThatStartedThisActivity.hasExtra("customerId")) {
                customerData = new TempCustomerData(intentThatStartedThisActivity.getStringExtra("customerId"), new ViewPanelGetDataListener());
            } else {
                    Log.e(TAG, "No customer id");
                    Toast.makeText(this, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void initializeUI(Customer customer) {
        if(currentPanel >= customer.getPanels().size()) {
            Log.e(TAG, "current Panel greater then list size");
            Toast.makeText(this, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
            goBackToElectricalActivity();
            return;
        }
        selectedPanel = customer.getPanels().get(currentPanel);
        panelAdapter = new PanelAdapter(selectedPanel, this);
        recyclerList.setAdapter(panelAdapter);

        //Set Panel Title
        panelTitle.setText(selectedPanel.getPanelTitle());

        //Attach onClick Listener to panelTitle
        panelTitle.setOnClickListener(panelTitleOnClickListener);
        setTitle(selectedPanel.getPanelDescription());
    }
    private void checkIntentForDeletedBreaker() {
        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra("actionType")) {
            //Deleting a Breaker
            if(intentThatStartedThisActivity.getStringExtra("actionType").equals("Delete Breaker")) {
                if(intentThatStartedThisActivity.hasExtra("breakerNumber")) {
                    int breakerToDelete = Integer.parseInt(intentThatStartedThisActivity.getStringExtra("breakerNumber"));
                    Log.i(TAG, "Delete Breaker " + breakerToDelete);
                   // deleteBreaker(breakerToDelete);
                }
            }
        }
    }
    // Mark:- OnClickListeners
    @Override
    public boolean onSupportNavigateUp(){
        Log.i(TAG, "Navigate Up");
        goBackToElectricalActivity();
        return true;
    }
    @Override
    public void onBackPressed()
    {
        goBackToElectricalActivity();
    }
    private void goBackToElectricalActivity() {
        Context context = ViewPanelActivity.this;
        Class destinationClass = ElectricalActivity.class;
        Intent intentToStartElectricalActivity = new Intent(context, destinationClass);
        intentToStartElectricalActivity.putExtra("isContractor", isContractorViewingPage);
        intentToStartElectricalActivity.putExtra("customerId", customerData.getUid());
        startActivity(intentToStartElectricalActivity);
        finish();
    }
    //Go To BreakerDetailActivity
    @Override
    public void onClick(Breaker breakerClicked) {
        Log.i(TAG, "Click Breaker " + breakerClicked.getNumber());
        Context context = this;
        Class destinationClass = BreakerDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        passBreakerData(intentToStartDetailActivity, breakerClicked);
        startActivity(intentToStartDetailActivity);
    }
    //Go to PanelDetailActivity
    private View.OnClickListener panelTitleOnClickListener = new View.OnClickListener() {
        public void onClick(View view){
            Log.i(TAG, "Click Panel Title");
            Intent intentToStartDetailActivity = createPanelDetailIntent();
            if(intentToStartDetailActivity != null) {
                startActivity(intentToStartDetailActivity);
                finish();
            }
        }
    };

    private void deleteBreaker(int breakerToDelete) {
        Panel updatedPanel = selectedPanel.deleteBreaker(breakerToDelete);
        customerData.updatePanel(currentPanel, updatedPanel);
        selectedPanel = updatedPanel;
        if(panelAdapter != null) {
            panelAdapter.notifyDataSetChanged();
        }
    }

    // Mak:- Intent Builders
    private void passBreakerData(Intent intent, Breaker breaker) {
        intent.putExtra("breakerDescription", breaker.getBreakerDescription());
        intent.putExtra("breakerNumber", breaker.getNumber());
        intent.putExtra("breakerAmperage", breaker.getAmperage());
        intent.putExtra("breakerType", breaker.getBreakerType());
        intent.putExtra("isContractor", isContractorViewingPage);
        intent.putExtra("customerId", customerData.getUid());
    }
    private Intent createPanelDetailIntent() {
        Intent intent = new Intent(this, PanelDetailActivity.class);
        intent.putExtra("panelId", currentPanel);
        if(selectedPanel != null) {
            intent.putExtra("panelDescription", selectedPanel.getPanelDescription());
            intent.putExtra("numberOfBreakers", selectedPanel.getBreakerList().size());
            intent.putExtra("isMainPanel", selectedPanel.getIsMainPanel());
            intent.putExtra("panelInstallDate", selectedPanel.getInstallDate());
            intent.putExtra("panelAmperage", selectedPanel.getAmperage());
            intent.putExtra("manufacturer", selectedPanel.getManufacturer());
        } else {
            Log.e(TAG, "selectedPanel is null");
            return null;
        }
        intent.putExtra("isContractor", isContractorViewingPage);
        intent.putExtra("customerId", customerData.getUid());
        return intent;
    }

    private class ViewPanelGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {
            Log.d(TAG, "Getting Customer Data");
        }
        @Override
        public void onSuccess(DataSnapshot data) {
            Log.d(TAG, "Received Customer Data");
            initializeUI(data.getValue(Customer.class));
        }
        @Override
        public void onFailed(DatabaseError databaseError) {
            Log.e(TAG, databaseError.toString());
            Toast.makeText(ViewPanelActivity.this, "Oops...something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }
}
