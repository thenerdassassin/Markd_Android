package com.schmidthappens.markd.customer_menu_activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.schmidthappens.markd.AdapterClasses.ServiceHistoryRecyclerViewAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_subactivities.ServiceDetailActivity;
import com.schmidthappens.markd.data_objects.ContractorService;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.utilities.StringUtilities;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;

import java.util.ArrayList;
import java.util.List;

public class ServiceHistoryActivity extends AppCompatActivity {
    private final static String TAG = "ServiceHistoryActivity";

    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    private boolean isContractorViewingPage;
    String contractorType;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_service_history_view);
        authentication = new FirebaseAuthentication(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
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

    // Mark: Setup
    private void processIntent(Intent intentToProcess) {
        if(intentToProcess.getBooleanExtra("isContractor", false)) {
            isContractorViewingPage = true;
            new ActionBarInitializer(this, true, "contractor");
            // Set Up Contractor Type
            if(intentToProcess.hasExtra("contractorType")) {
                contractorType = intentToProcess.getStringExtra("contractorType");
            } else {
                Log.e(TAG, "No Contractor Type");
                Toast.makeText(this, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
            }
            // Set up Customer Info
            if(intentToProcess.hasExtra("customerId")) {
                customerData = new TempCustomerData(
                        intentToProcess.getStringExtra("customerId"),
                        new ServiceHistoryGetDataListener());
            } else {
                Log.e(TAG, "No customer id");
                Toast.makeText(this, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            isContractorViewingPage = false;
            new ActionBarInitializer(this, true, "customer");
            customerData = new TempCustomerData(authentication, new ServiceHistoryGetDataListener());
        }
    }
    public void initializeRecyclerView() {
        final RecyclerView serviceHistoryRecyclerView = findViewById(R.id.service_history_recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        serviceHistoryRecyclerView.setLayoutManager(layoutManager);
        serviceHistoryRecyclerView.setHasFixedSize(false);

        if (isContractorViewingPage) {
            final List<ContractorService> services = customerData.getServices(contractorType);
            serviceHistoryRecyclerView.setAdapter(
                    new ServiceHistoryRecyclerViewAdapter(this, contractorType, services));
        } else {
            final List<ContractorService> electricalServices =
                    customerData.getElectricalServices() == null ?
                            new ArrayList<ContractorService>() :
                            customerData.getElectricalServices();

            final List<ContractorService> plumbingServices =
                    customerData.getPlumbingServices() == null ?
                            new ArrayList<ContractorService>() :
                            customerData.getPlumbingServices();

            final List<ContractorService> hvacServices =
                    customerData.getHvacServices() == null ?
                            new ArrayList<ContractorService>() :
                            customerData.getHvacServices();

            final List<ContractorService> paintingServices =
                    customerData.getPaintingServices() == null ?
                            new ArrayList<ContractorService>() :
                            customerData.getPaintingServices();
            serviceHistoryRecyclerView.setAdapter(
                    new ServiceHistoryRecyclerViewAdapter(this,
                            plumbingServices,
                            hvacServices,
                            electricalServices,
                            paintingServices));
        }
    }

    /**
     * Go to ServiceDetailActivity to  create new ContractorService
     * @param serviceType - Contractor Type for Service
     */
    public void addNewService(final String serviceType) {
        final Intent intentToReturn = new Intent(this, ServiceDetailActivity.class);
        intentToReturn.putExtra("serviceType", serviceType);
        intentToReturn.putExtra("isContractor", isContractorViewingPage);
        intentToReturn.putExtra("customerId", customerData.getUid());
        startActivity(intentToReturn);
    }

    /**
     * Go to ServiceDetailActivity with ContractorService
     * @param service - Contractor Service to Edit
     * @param serviceType - Contractor Type for Service
     * @param position - index of service
     */
    public void getServiceDetail(
            @NonNull final ContractorService service,
            final String serviceType,
            final int position) {
        final Intent intentToReturn = new Intent(this, ServiceDetailActivity.class);
        intentToReturn.putExtra("serviceId", position);
        intentToReturn.putExtra("contractor", service.getContractor());
        intentToReturn.putExtra("serviceDate",
                StringUtilities.getDateString(
                        service.getMonth(),
                        service.getDay(),
                        service.getYear()
                ));
        intentToReturn.putExtra("description", service.getComments());
        intentToReturn.putExtra("isContractor", isContractorViewingPage);
        intentToReturn.putExtra("customerId", customerData.getUid());
        intentToReturn.putExtra("serviceType", serviceType);
        startActivity(intentToReturn);
    }

    private class ServiceHistoryGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() { }
        @Override
        public void onSuccess(final DataSnapshot data) {
            initializeRecyclerView();
        }
        @Override
        public void onFailed(final DatabaseError databaseError) { }
    }
}
