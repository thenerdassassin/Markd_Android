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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.schmidthappens.markd.AdapterClasses.PaintListAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.PaintSurface;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.customer_subactivities.PaintEditActivity;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.schmidthappens.markd.view_initializers.ContractorFooterViewInitializer;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;

import java.util.List;

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

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_painting_view);

        authentication = new FirebaseAuthentication(this);

        if(getIntent().hasExtra("isContractor")) {
            new ActionBarInitializer(this, true, "contractor");
        } else {
            new ActionBarInitializer(this, true, "customer");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        customerData = new TempCustomerData(authentication, new PaintingGetDataListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
    }

    // Mark: Setup
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
        View listOfExteriorPaints = new PaintListAdapter().createPaintListView(this, customerData.getExteriorSurfaces(), true);
        exteriorPaintList.addView(listOfExteriorPaints);

        //Set Up Interior PaintList
        interiorPaintList = (FrameLayout)findViewById(R.id.painting_interior_paint_list);
        View listOfInteriorPaints = new PaintListAdapter().createPaintListView(this, customerData.getInteriorSurfaces(), false);
        interiorPaintList.addView(listOfInteriorPaints);
    }
    public void initializeFooter() {
        paintingContractor = (FrameLayout)findViewById(R.id.painting_footer);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.mdf_logo);
        ContractorDetails painter = customerData.getPainter();
        if(painter == null) {
            View v = ContractorFooterViewInitializer.createFooterView(this);
            paintingContractor.addView(v);
        } else {
            View v = ContractorFooterViewInitializer.createFooterView(this, logo, painter.getCompanyName(), painter.getTelephoneNumber(), painter.getWebsiteUrl());
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
            activityContext.startActivity(intentToStartPaintEditActivity);
        }
    };

    public void deletePaintSurface(int position, boolean isExterior) {
        Log.i(TAG, "{Delete Paint Item:" + position + " isExterior:" + isExterior + "}");
        //Used to reset the adapter
        if(isExterior) {
            exteriorPaintList.removeAllViews();
            exteriorPaintList.addView(new PaintListAdapter().createPaintListView(this, customerData.getExteriorSurfaces(), true));
        } else {
            interiorPaintList.removeAllViews();
            interiorPaintList.addView(new PaintListAdapter().createPaintListView(this, customerData.getInteriorSurfaces(), false));
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
