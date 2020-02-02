package com.markd.applications.android.home.customer_menu_activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.markd.applications.android.home.AdapterClasses.PaintingRecyclerViewAdapter;
import com.markd.applications.android.home.R;
import com.markd.applications.android.home.account_authentication.FirebaseAuthentication;
import com.markd.applications.android.home.account_authentication.LoginActivity;
import com.markd.applications.android.home.customer_subactivities.PaintEditActivity;
import com.markd.applications.android.home.data_objects.Contractor;
import com.markd.applications.android.home.data_objects.ContractorDetails;
import com.markd.applications.android.home.data_objects.PaintSurface;
import com.markd.applications.android.home.data_objects.TempCustomerData;
import com.markd.applications.android.home.utilities.OnGetDataListener;
import com.markd.applications.android.home.view_initializers.ActionBarInitializer;
import com.markd.applications.android.home.view_initializers.ContractorFooterViewInitializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 5/29/2017.
 */

public class PaintingActivity extends AppCompatActivity {
    private final static String TAG = "PaintingActivity";

    private FrameLayout paintingContractor;
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
                customerData = new TempCustomerData(
                        intentToProcess.getStringExtra("customerId"),
                        new PaintingGetDataListener());
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
        initializePaintingRecycler();
        initializeFooter();

    }
    public void initializePaintingRecycler() {
        final RecyclerView paintingRecycler = findViewById(R.id.painting_recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        paintingRecycler.setLayoutManager(layoutManager);
        paintingRecycler.setHasFixedSize(false);

        final List<PaintSurface> exteriorSurfaces =
                customerData.getExteriorSurfaces() == null ?
                        new ArrayList<PaintSurface>() :
                        customerData.getExteriorSurfaces();
        final List<PaintSurface> interiorSurfaces =
                customerData.getInteriorSurfaces() == null ?
                        new ArrayList<PaintSurface>() :
                        customerData.getInteriorSurfaces();

        paintingRecycler.setAdapter(
                new PaintingRecyclerViewAdapter(
                        this,
                        exteriorSurfaces,
                        interiorSurfaces));
    }
    public void initializeFooter() {
        paintingContractor = findViewById(R.id.painting_footer);
        if(!customerData.getPainter(new OnGetDataListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "Getting painter data");
            }

            @Override
            public void onSuccess(final DataSnapshot data) {
                final Contractor painter = data.getValue(Contractor.class);
                Log.d(TAG, "data:" + data.toString());
                if(painter == null || painter.getContractorDetails() == null) {
                    Log.d(TAG, "No painter data");
                    paintingContractor
                            .addView(ContractorFooterViewInitializer
                                    .createFooterView(
                                            PaintingActivity.this, "Painter"));
                } else {
                    final ContractorDetails contractorDetails = painter.getContractorDetails();
                    final String pathToLogoFile = "logos/" + data.getKey() + "/" + painter.getLogoFileName();
                    paintingContractor
                            .addView(ContractorFooterViewInitializer
                                    .createFooterView(PaintingActivity.this,
                                            contractorDetails.getCompanyName(),
                                            contractorDetails.getTelephoneNumber(),
                                            contractorDetails.getWebsiteUrl(),
                                            pathToLogoFile));
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
    public void editPaintSurface(
            final PaintSurface surfaceToEdit, boolean isExterior, int index) {
        final Intent intentToStartPaintEditActivity =
                new Intent(this, PaintEditActivity.class);
        putPaintObjectInIntent(surfaceToEdit, intentToStartPaintEditActivity, index);
        intentToStartPaintEditActivity.putExtra("isExterior", isExterior);
        startActivity(intentToStartPaintEditActivity);
    }
    private void putPaintObjectInIntent(
            final PaintSurface paintSurface, final Intent intent, int position) {
        intent.putExtra("id", position);
        intent.putExtra("location", paintSurface.getLocation());
        intent.putExtra("paintDate", paintSurface.getDateString());
        intent.putExtra("brand", paintSurface.getBrand());
        intent.putExtra("color", paintSurface.getColor());
        intent.putExtra("customerId", customerData.getUid());
    }
    public void addPaintSurface(boolean isExterior) {
        final Intent intentToStartPaintEditActivity = new Intent(this, PaintEditActivity.class);
        intentToStartPaintEditActivity.putExtra("isNew", true);
        intentToStartPaintEditActivity.putExtra("isExterior", isExterior);
        intentToStartPaintEditActivity.putExtra("customerId", customerData.getUid());
        intentToStartPaintEditActivity.putExtra("isContractor", isContractorViewingPage);
        startActivity(intentToStartPaintEditActivity);
    }

    private class PaintingGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {

        }
        @Override
        public void onSuccess(final DataSnapshot data) {
            initializeUI();
        }
        @Override
        public void onFailed(final DatabaseError databaseError) {

        }
    }
}
