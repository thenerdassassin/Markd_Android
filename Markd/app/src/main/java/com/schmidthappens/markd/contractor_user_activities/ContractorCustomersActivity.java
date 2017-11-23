package com.schmidthappens.markd.contractor_user_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.AdapterClasses.CustomerListRecyclerViewAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.Customer;
import com.schmidthappens.markd.data_objects.TempContractorData;
import com.schmidthappens.markd.utilities.CustomerGetter;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/30/17.
 */

public class ContractorCustomersActivity extends AppCompatActivity {
    private final static String TAG = "ContractorCustomersActv";
    FirebaseAuthentication authentication;
    TempContractorData contractorData;

    private RecyclerView customerRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contractor_customers_view);
        authentication = new FirebaseAuthentication(this);
        new ActionBarInitializer(this, false, "contractor");
        customerRecyclerView = (RecyclerView)findViewById(R.id.contractor_customers_recycler_view);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        contractorData = new TempContractorData((authentication.getCurrentUser().getUid()), new ContractorCustomersActivity.ContractorCustomersGetDataListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        customerRecyclerView.setLayoutManager(layoutManager);
        customerRecyclerView.setHasFixedSize(true);
        customerRecyclerView.addItemDecoration(new DividerItemDecoration(ContractorCustomersActivity.this, DividerItemDecoration.VERTICAL));
        final List<String> customerReferences = contractorData.getCustomers();

        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG, dataSnapshot.toString());
                List<Customer> customers = CustomerGetter.getCustomersFromReferences(customerReferences, dataSnapshot);
                customerRecyclerView.setAdapter(
                        new CustomerListRecyclerViewAdapter(ContractorCustomersActivity.this, contractorData.getType(), customers, customerReferences)
                );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
                Toast.makeText(ContractorCustomersActivity.this, "Oops..something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ContractorCustomersGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {
            Log.v(TAG, "Getting Temp Customer Data");
        }

        @Override
        public void onSuccess(DataSnapshot data) {
            Log.v(TAG, "Got Temp Customer Data");
            Log.d(TAG, data.toString());
            setUpRecyclerView();
        }

        @Override
        public void onFailed(DatabaseError databaseError) {
            Log.e(TAG, databaseError.toString());
        }
    }
}
