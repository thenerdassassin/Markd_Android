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

import com.schmidthappens.markd.AdapterClasses.PaintListAdapter;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.PaintSurface;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.customer_subactivities.PaintEditActivity;
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

    private List<PaintSurface> exteriorPaintSurfaces = TempCustomerData.getInstance().getExteriorSurfaces();
    private List<PaintSurface> interiorPaintSurfaces = TempCustomerData.getInstance().getInteriorSurfaces();
    private ContractorDetails painter = TempCustomerData.getInstance().getPainter();
    private final static String TAG = "PaintingActivity";

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.menu_activity_painting_view);

        SessionManager sessionManager = new SessionManager(PaintingActivity.this);
        sessionManager.checkLogin();
        new ActionBarInitializer(this, true);

        //Initialize Exterior Add Button
        addExteriorPaintButton = (ImageView)findViewById(R.id.painting_exterior_add_button);
        addExteriorPaintButton.setOnClickListener(addExteriorPaintOnClickListener);

        //Set Up Exterior PaintList
        exteriorPaintList = (FrameLayout)findViewById(R.id.painting_exterior_paint_list);
        View listOfExteriorPaints = new PaintListAdapter().createPaintListView(this, exteriorPaintSurfaces, true);
        exteriorPaintList.addView(listOfExteriorPaints);

        //Initialize Interior Add Button
        addInteriorPaintButton = (ImageView)findViewById(R.id.painting_interior_add_button);
        addInteriorPaintButton.setOnClickListener(addInteriorPaintOnClickListener);

        //Set Up Interior PaintList
        interiorPaintList = (FrameLayout)findViewById(R.id.painting_interior_paint_list);
        View listOfInteriorPaints = new PaintListAdapter().createPaintListView(this, interiorPaintSurfaces, false);
        interiorPaintList.addView(listOfInteriorPaints);

        //Initialize Contractor Footer
        paintingContractor = (FrameLayout)findViewById(R.id.painting_footer);
        Drawable logo = ContextCompat.getDrawable(this, R.drawable.mdf_logo);
        View v = ContractorFooterViewInitializer.createFooterView(this, logo, painter.getCompanyName(), painter.getTelephoneNumber(), painter.getWebsiteUrl());
        paintingContractor.addView(v);
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
            exteriorPaintList.addView(new PaintListAdapter().createPaintListView(this, TempCustomerData.getInstance().getExteriorSurfaces(), true));
        } else {
            interiorPaintList.removeAllViews();
            interiorPaintList.addView(new PaintListAdapter().createPaintListView(this, TempCustomerData.getInstance().getInteriorSurfaces(), false));
        }
    }
}
