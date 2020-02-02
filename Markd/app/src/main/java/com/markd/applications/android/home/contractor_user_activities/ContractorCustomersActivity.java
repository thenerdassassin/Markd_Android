package com.markd.applications.android.home.contractor_user_activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.markd.applications.android.home.AdapterClasses.CustomerListRecyclerViewAdapter;
import com.markd.applications.android.home.R;
import com.markd.applications.android.home.account_authentication.FirebaseAuthentication;
import com.markd.applications.android.home.account_authentication.LoginActivity;
import com.markd.applications.android.home.billing.BillingClientHandler;
import com.markd.applications.android.home.customer_menu_activities.ServiceHistoryActivity;
import com.markd.applications.android.home.data_objects.Customer;
import com.markd.applications.android.home.data_objects.TempContractorData;
import com.markd.applications.android.home.utilities.ContractorUtilities;
import com.markd.applications.android.home.utilities.CustomerGetter;
import com.markd.applications.android.home.utilities.CustomerSelectedInterface;
import com.markd.applications.android.home.utilities.DateUtitilities;
import com.markd.applications.android.home.utilities.OnGetDataListener;
import com.markd.applications.android.home.view_initializers.ActionBarInitializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Josh Schmidt on 9/30/17.
 */

public class ContractorCustomersActivity extends AppCompatActivity implements CustomerSelectedInterface{
    private final static String TAG = "ContractorCustomersActv";
    FirebaseAuthentication authentication;
    TempContractorData contractorData;
    BillingClientHandler billingClient;

    private RecyclerView customerRecyclerView;
    private AppCompatButton messageAllButton;
    private TextView noCustomerTextView;

    private List<Customer> customers;
    private List<String> customerReferences;

    private AlertDialog customerSelectedDialog;
    private AlertDialog purchaseAlertDialog;
    private final String[] alertDialogOptions = {
            "Send Notification",
            "Edit Home Details",
            "Edit Service History"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contractor_customers_view);
        authentication = new FirebaseAuthentication(this);
        new ActionBarInitializer(this, false, "contractor");
        initializeXmlObjects();

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
        contractorData = new TempContractorData((authentication.getCurrentUser().getUid()),
                new ContractorCustomersActivity.ContractorCustomersGetDataListener());
        billingClient = new BillingClientHandler(this,
                new ContractorCustomersActivity.SubscriptionPurchasesListener());
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
        billingClient.endConnection();

        if (purchaseAlertDialog != null && purchaseAlertDialog.isShowing()) {
            purchaseAlertDialog.dismiss();
        }
        if (customerSelectedDialog != null && customerSelectedDialog.isShowing()) {
            customerSelectedDialog.dismiss();
        }
    }

    private void initializeXmlObjects() {
        customerRecyclerView = findViewById(R.id.contractor_customers_recycler_view);
        noCustomerTextView = findViewById(R.id.contractor_customers_empty_list);
        ((EditText)findViewById(R.id.customer_filter)).setOnEditorActionListener(
                (TextView textView, int i, KeyEvent keyEvent) -> {
                    final String lastNameFilter = textView.getText().toString();
                    Log.d(TAG, "LastName - " + textView.getText().toString());
                    customerRecyclerView.setAdapter(
                            new CustomerListRecyclerViewAdapter(
                                    ContractorCustomersActivity.this,
                                    customers,
                                    customerReferences,
                                    lastNameFilter));
                    Log.d(TAG, "Customers size:" + customers.size());
                    return false;
                });
        messageAllButton = findViewById(R.id.contractor_customers_message_all);
        messageAllButton.setOnClickListener(messageAllClickListener);
    }
    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        customerRecyclerView.setLayoutManager(layoutManager);
        customerRecyclerView.setHasFixedSize(true);
        customerRecyclerView.addItemDecoration(new DividerItemDecoration(ContractorCustomersActivity.this, DividerItemDecoration.VERTICAL));
        customerReferences = contractorData.getCustomers();
        if(customerReferences != null && customerReferences.size() > 0) {
            noCustomerTextView.setVisibility(View.GONE);
            FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.v(TAG, dataSnapshot.toString());
                    final Map<String, Customer> customerMap = CustomerGetter
                            .getCustomersFromReferences(customerReferences, dataSnapshot);
                    customers = new ArrayList<>(customerMap.values());
                    customerReferences = new ArrayList<>(customerMap.keySet());
                    customerRecyclerView.setAdapter(
                            new CustomerListRecyclerViewAdapter(
                                    ContractorCustomersActivity.this,
                                    customers,
                                    customerReferences));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, databaseError.toString());
                    Toast.makeText(ContractorCustomersActivity.this, "Oops..something went wrong.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            noCustomerTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCustomerSelected(String customerId) {
        if (isSubscribed()) {
            showAlertDialog(customerId);
        } else {
            showPurchaseAlert();
        }
    }
    private void showAlertDialog(final String customerId) {
        customerSelectedDialog = new AlertDialog.Builder(ContractorCustomersActivity.this)
                .setTitle("What would you like to do?")
                .setItems(alertDialogOptions, (DialogInterface dialog, int which) -> {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if(which == 0) {
                            Log.i(TAG, "Push Notification:" + customerId);
                            goToNotificationsPage(customerId);
                        } else if (which == 1) {
                            Log.i(TAG, "Go to page:" + customerId);
                            editHomeDetails(customerId);
                        } else if (which == 2) {
                            Log.i(TAG, "Edit Service History: " + customerId);
                            editServiceHistory(customerId);
                        }
                }).create();
        customerSelectedDialog.show();
    }
    private void goToNotificationsPage(final String customerId) {
        final Intent goToSendNotifactionsPage = new Intent(this, SendNotificationsActivity.class)
                .putExtra("customerId", customerId);
        startActivity(goToSendNotifactionsPage);
    }
    private void editHomeDetails(final String customerId) {
        final Class contractorClass = ContractorUtilities.getClassFromContractorType(contractorData.getType());
        if(contractorClass == null) {
            Log.e(TAG, "contractorClass is null");
        } else {
            Intent goToCustomerPage = new Intent(this, contractorClass)
                    .putExtra("isContractor", true)
                    .putExtra("customerId", customerId);
            startActivity(goToCustomerPage);
        }
    }
    private void editServiceHistory(final String customerId) {
        final Intent goToCustomerPage = new Intent(this, ServiceHistoryActivity.class)
                    .putExtra("contractorType", contractorData.getType())
                    .putExtra("isContractor", true)
                    .putExtra("customerId", customerId);
        startActivity(goToCustomerPage);
    }
    private class ContractorCustomersGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {
            Log.v(TAG, "Getting TempContractorData");
        }

        @Override
        public void onSuccess(DataSnapshot data) {
            Log.v(TAG, "Got TempContractorData");
            Log.d(TAG, data.toString());
            setUpRecyclerView();
            if(isSubscribed()) {
                messageAllButton.setText("Message All");
            } else {
                messageAllButton.setText("Subscribe");
            }
        }

        @Override
        public void onFailed(DatabaseError databaseError) {
            Log.e(TAG, databaseError.toString());
        }
    }
    private class SubscriptionPurchasesListener implements PurchasesUpdatedListener {
        private final Date currentDate;

        public SubscriptionPurchasesListener() {
            this.currentDate = DateUtitilities.getCurrentDate();
        }

        @Override
        public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
            if (billingResult == null || list == null || list.isEmpty()) {
                handleError();
                return;
            }

            switch(billingResult.getResponseCode()) {
                case BillingClient.BillingResponseCode.OK:
                case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                    handlePurchase(list.get(0));
                    break;
                case BillingClient.BillingResponseCode.USER_CANCELED:
                case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
                    handleNonPurchase();
                    break;
                case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                case BillingClient.BillingResponseCode.ERROR:
                    handleError();
                    break;
            }
        }

        private void handlePurchase(final Purchase purchase) {
            contractorData.updateSubscriptionExpiration(
                    DateUtitilities.addDays(currentDate, 365));
            billingClient.handlePurchase(purchase);
        }

        private void handleNonPurchase() {
            // If unsuccessful purchase then set subscription to
            // 4 days ago.
            contractorData.updateSubscriptionExpiration(
                    DateUtitilities.addDays(currentDate, -4));
        }

        private void handleError() {
            Toast.makeText(ContractorCustomersActivity.this,
                    "Oops...something went wrong",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener messageAllClickListener = view -> {
        if (isSubscribed()) {
            startActivity(new Intent(
                    ContractorCustomersActivity.this,
                    SendNotificationsActivity.class));
        } else {
            showPurchaseAlert();
        }
    };

    private boolean isSubscribed() {
        if (contractorData == null ||
                contractorData.getSubscriptionExpiration() == null) {
            return false;
        } else {
            final Date expirationDate = contractorData.getSubscriptionExpiration();
            final Date threeDaysInFuture = DateUtitilities
                    .addDays(DateUtitilities.getCurrentDate(), 3);
            // Give them three days to renew subscription otherwise show purchase alert
            return threeDaysInFuture.compareTo(expirationDate) < 0;
        }
    }

    private void showPurchaseAlert() {
        if (customerSelectedDialog != null && customerSelectedDialog.isShowing()) {
            customerSelectedDialog.dismiss();
        }
        purchaseAlertDialog = new AlertDialog
                .Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                .setTitle("Purchase Subscription")
                .setMessage("To send notifications to customers or log serviced done on a " +
                        " customer's home a subscription is needed.\nAnnual Subscription costs\n" +
                        " $199.99 per year")
                .setNegativeButton("Not Now", (DialogInterface dialog, int which) -> {
                    contractorData.updateSubscriptionExpiration(DateUtitilities
                            .addDays(DateUtitilities.getCurrentDate(), -1));
                })
                .setPositiveButton("Subscribe", (DialogInterface dialog, int which) -> {
                    Log.d(TAG, "Starting billing flow");
                    billingClient.launchBillingFlow();
                }).create();
        purchaseAlertDialog.show();
    }
}
