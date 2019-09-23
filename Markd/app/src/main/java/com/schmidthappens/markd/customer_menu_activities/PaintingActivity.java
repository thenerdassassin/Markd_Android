package com.schmidthappens.markd.customer_menu_activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.schmidthappens.markd.AdapterClasses.PaintListAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_subactivities.PaintEditActivity;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.schmidthappens.markd.view_initializers.ContractorFooterViewInitializer;

/**
 * Created by Josh on 5/29/2017.
 */

public class PaintingActivity extends AppCompatActivity {
    //XML Objects
    ImageView addExteriorPaintButton;
    FrameLayout exteriorPaintList;

    ImageView addInteriorPaintButton;
    FrameLayout interiorPaintList;
    private FrameLayout paintingContractor;

    private final static String TAG = "PaintingActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    private boolean isContractorViewingPage;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_painting_view);
        authentication = new FirebaseAuthentication(this);
    }


    @Override
    public void onStart() {
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

    // Mark: Setup
    private void processIntent(Intent intentToProcess) {
        if(intentToProcess.hasExtra("isContractor")) {
            isContractorViewingPage = true;
            new ActionBarInitializer(this, true, "contractor");
            if(intentToProcess.hasExtra("customerId")) {
                customerData = new TempCustomerData(intentToProcess.getStringExtra("customerId"), new PaintingGetDataListener());
            } else {
                Log.e(TAG, "No customer id");
                Toast.makeText(this, "Oops...something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            isContractorViewingPage = false;
            new ActionBarInitializer(this, true, "customer");
            customerData = new TempCustomerData(authentication, new PaintingGetDataListener());
        }
    }
    public void initializeUI() {
        initializeButtons();
        initializePaintLists();
        initializeFooter();

    }
    public void initializeButtons() {
        //Initialize Exterior Add Button
        addExteriorPaintButton = (ImageView)findViewById(R.id.painting_exterior_add_button);
        addExteriorPaintButton.setOnClickListener(addExteriorPaintOnClickListener);

        //Initialize Interior Add Button
        addInteriorPaintButton = (ImageView)findViewById(R.id.painting_interior_add_button);
        addInteriorPaintButton.setOnClickListener(addInteriorPaintOnClickListener);
    }
    public void initializePaintLists() {
        //Set Up Exterior PaintList
        exteriorPaintList = (FrameLayout)findViewById(R.id.painting_exterior_paint_list);
        View listOfExteriorPaints = new PaintListAdapter().createPaintListView(this, customerData.getExteriorSurfaces(), true, customerData.getUid());
        exteriorPaintList.addView(listOfExteriorPaints);

        //Set Up Interior PaintList
        interiorPaintList = (FrameLayout)findViewById(R.id.painting_interior_paint_list);
        View listOfInteriorPaints = new PaintListAdapter().createPaintListView(this, customerData.getInteriorSurfaces(), false, customerData.getUid());
        interiorPaintList.addView(listOfInteriorPaints);
    }
    public void initializeFooter() {
        paintingContractor = (FrameLayout)findViewById(R.id.painting_footer);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.mdf_logo);
        if(!customerData.getPainter(new OnGetDataListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "Getting painter data");
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                Contractor painter = data.getValue(Contractor.class);
                Log.d(TAG, "data:" + data.toString());
                if(painter == null || painter.getContractorDetails() == null) {
                    Log.d(TAG, "No painter data");
                    View v = ContractorFooterViewInitializer.createFooterView(PaintingActivity.this, "Painter");
                    paintingContractor.addView(v);
                } else {
                    ContractorDetails contractorDetails = painter.getContractorDetails();
                    final String pathToLogoFile = "logos/" + data.getKey() + "/" + painter.getLogoFileName();
                    View v = ContractorFooterViewInitializer.createFooterView(PaintingActivity.this,
                            contractorDetails.getCompanyName(),
                            contractorDetails.getTelephoneNumber(),
                            contractorDetails.getWebsiteUrl(),
                            pathToLogoFile);
                    paintingContractor.addView(v);
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
                View v = ContractorFooterViewInitializer.createFooterView(PaintingActivity.this, "Painter");
                paintingContractor.addView(v);
            }
        })) {
            Log.d(TAG, "No painter data");
            View v = ContractorFooterViewInitializer.createFooterView(PaintingActivity.this, "Painter");
            paintingContractor.addView(v);
        }
    }

    // Mark: OnClickListeners
    private View.OnClickListener addExteriorPaintOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context activityContext = PaintingActivity.this;
            Class destinationClass = PaintEditActivity.class;

            Intent intentToStartPaintEditActivity = new Intent(activityContext, destinationClass);
            intentToStartPaintEditActivity.putExtra("isNew", true);
            intentToStartPaintEditActivity.putExtra("isExterior", true);
            intentToStartPaintEditActivity.putExtra("customerId", customerData.getUid());
            intentToStartPaintEditActivity.putExtra("isContractor", isContractorViewingPage);
            activityContext.startActivity(intentToStartPaintEditActivity);
        }
    };

    private View.OnClickListener addInteriorPaintOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context activityContext = PaintingActivity.this;
            Class destinationClass = PaintEditActivity.class;

            Intent intentToStartPaintEditActivity = new Intent(activityContext, destinationClass);
            intentToStartPaintEditActivity.putExtra("isNew", true);
            intentToStartPaintEditActivity.putExtra("customerId", customerData.getUid());
            intentToStartPaintEditActivity.putExtra("isContractor", isContractorViewingPage);
            activityContext.startActivity(intentToStartPaintEditActivity);
        }
    };

    public void deletePaintSurface(int position, boolean isExterior) {
        Log.i(TAG, "{Delete Paint Item:" + position + " isExterior:" + isExterior + "}");
        //Used to reset the adapter
        if(isExterior) {
            exteriorPaintList.removeAllViews();
            exteriorPaintList.addView(new PaintListAdapter().createPaintListView(this, customerData.getExteriorSurfaces(), true, customerData.getUid()));
        } else {
            interiorPaintList.removeAllViews();
            interiorPaintList.addView(new PaintListAdapter().createPaintListView(this, customerData.getInteriorSurfaces(), false, customerData.getUid()));
        }
    }

    private class PaintingGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {

        }
        @Override
        public void onSuccess(DataSnapshot data) {
            initializeUI();
        }
        @Override
        public void onFailed(DatabaseError databaseError) {

        }
    }
}
