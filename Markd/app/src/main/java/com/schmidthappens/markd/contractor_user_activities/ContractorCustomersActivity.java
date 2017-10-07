package com.schmidthappens.markd.contractor_user_activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.schmidthappens.markd.AdapterClasses.CustomerListRecyclerViewAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.TempContractorData;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;

/**
 * Created by joshua.schmidtibm.com on 9/30/17.
 */

public class ContractorCustomersActivity extends AppCompatActivity {
    private final static String TAG = "ContractorCustomersActivity";
    private RecyclerView customerRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contractor_customers_view);
        new ActionBarInitializer(this, false);

        SessionManager sessionManager = new SessionManager(ContractorCustomersActivity.this);
        sessionManager.checkLogin();

        customerRecyclerView = (RecyclerView)findViewById(R.id.contractor_customers_recycler_view);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        customerRecyclerView.setLayoutManager(layoutManager);
        customerRecyclerView.setHasFixedSize(true);
        customerRecyclerView.setAdapter(new CustomerListRecyclerViewAdapter(ContractorCustomersActivity.this, TempContractorData.getInstance().getCustomers()));
        customerRecyclerView.addItemDecoration(new DividerItemDecoration(ContractorCustomersActivity.this, DividerItemDecoration.VERTICAL));
    }
}
