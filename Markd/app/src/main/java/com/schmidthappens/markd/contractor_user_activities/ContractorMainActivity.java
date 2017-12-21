package com.schmidthappens.markd.contractor_user_activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.TempContractorData;
import com.schmidthappens.markd.file_storage.MarkdFirebaseStorage;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Josh on 9/16/2017.
 */

public class ContractorMainActivity extends AppCompatActivity {
    private final static String TAG = "ContractorMainActivity";
    FirebaseAuthentication authentication;
    TempContractorData contractorData;

    private FrameLayout logoFrame;
    private ImageView logoImage;
    private ImageView logoImagePlaceholder;

    private TextView companyNameTextView;
    private TextView companyTelephone;
    private TextView companyWebpage;
    private TextView companyZipCode;

    private boolean hasImage;
    private static final int IMAGE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contractor_main_view);

        authentication = new FirebaseAuthentication(this);
        new ActionBarInitializer(this, false, "contractor", editCompanyOnClickListener);

        logoFrame = (FrameLayout)findViewById(R.id.contractor_logo_frame);
        logoImage = (ImageView)findViewById(R.id.contractor_logo);
        logoImagePlaceholder = (ImageView)findViewById(R.id.contractor_logo_placeholder);

        companyNameTextView = (TextView)findViewById(R.id.contractor_company_name);
        companyTelephone = (TextView)findViewById(R.id.contractor_telephone_text);
        companyWebpage = (TextView)findViewById(R.id.contractor_website_textview);
        companyZipCode = (TextView)findViewById(R.id.contractor_zipcode_textview);
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

        contractorData = new TempContractorData((authentication.getCurrentUser().getUid()), new ContractorMainGetDataListener());
        logoFrame.setOnClickListener(photoClick);
        logoFrame.setOnLongClickListener(photoLongClick);
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
        contractorData.removeListener();
    }

    //Mark:- Photo Functions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String oldFileName = contractorData.getLogoFileName();
            String oldPath = "logos/" + authentication.getCurrentUser().getUid() + "/" + oldFileName;

            String fileName = contractorData.setLogoFileName();
            String path = "logos/" + authentication.getCurrentUser().getUid() + "/" + fileName;

            Uri photo = null;
            //TODO: camera result implementations
           /* if(temp.exists() && temp.length() > 0) {
                //Camera Result
                File temp = getTempFile();
                photo = Uri.fromFile(temp);
            } else {
                //Gallery Result
                photo = data.getData();
            }
            */
           photo = data.getData();
            if(photo != null) {
                MarkdFirebaseStorage.updateImage(ContractorMainActivity.this, path, photo, logoImage);
                MarkdFirebaseStorage.deleteImage(oldPath);
                Toast.makeText(this, "Updating Logo", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private Intent createPhotoOrGalleryChooserIntent() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");

        //TODO: implement camera intent
        //Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile()));

        String pickTitle = "Take or select a photo";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        return chooserIntent;
    }

    // Mark:- Action Listeners
    private View.OnLongClickListener photoLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            startActivityForResult(createPhotoOrGalleryChooserIntent(), IMAGE_REQUEST_CODE);
            return true;
        }
    };
    private View.OnClickListener photoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Only resets image if no image exists
            if(hasImage) {
                startActivityForResult(createPhotoOrGalleryChooserIntent(), IMAGE_REQUEST_CODE);
            }
        }
    };
    private View.OnClickListener editCompanyOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startContractorEditActivity();
        }
    };
    private void startContractorEditActivity() {
        Context activityContext = ContractorMainActivity.this;
        Class destinationClass = ContractorEditActivity.class;
        Intent gotToContractorEditActivityIntent = new Intent(activityContext, destinationClass);
        addContractorDataToIntent(gotToContractorEditActivityIntent);
        startActivity(gotToContractorEditActivityIntent);
    }
    private void addContractorDataToIntent(Intent intent) {
        if(contractorData != null) {
            ContractorDetails contractorDetails = contractorData.getContractorDetails();
            if(contractorDetails != null) {
                intent.putExtra("companyName", contractorDetails.getCompanyName());
                intent.putExtra("telephoneNumber", contractorDetails.getTelephoneNumber());
                intent.putExtra("websiteUrl", contractorDetails.getWebsiteUrl());
                intent.putExtra("zipCode", contractorDetails.getZipCode());
            }
        }
    }

    // Mark:- SetUp Functions
    private void initalizeUI() {
        Log.d(TAG, contractorData.toString());
        initializeTextViews(contractorData.getContractorDetails());
        hasImage = MarkdFirebaseStorage.loadImage(this, "logos/" + authentication.getCurrentUser().getUid() + "/" + contractorData.getLogoFileName(), logoImage);

        if(hasImage) {
            logoFrame.setBackgroundColor(Color.TRANSPARENT);
            logoImage.setLayoutParams(
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.START)
            );
            logoImagePlaceholder.setVisibility(View.GONE);
        } else {
            logoImagePlaceholder.setVisibility(View.VISIBLE);
        }
    }
    private void initializeTextViews(ContractorDetails contractorDetails) {
        if(contractorDetails == null) {
            Log.i(TAG, "ContractorDetails are null");
            startContractorEditActivity();
        } else {
            companyNameTextView.setText(contractorDetails.getCompanyName());
            companyTelephone.setText(contractorDetails.getTelephoneNumber());
            companyWebpage.setText(contractorDetails.getWebsiteUrl());
            companyZipCode.setText(contractorDetails.getZipCode());
        }
    }
    private class ContractorMainGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {
            Log.i(TAG, "Waiting for Data");
        }

        @Override
        public void onSuccess(DataSnapshot data) {
            Log.d(TAG, "Got Data");
            initalizeUI();
        }

        @Override
        public void onFailed(DatabaseError databaseError) {
            Log.e(TAG, "Failed to get Data");
        }
    }
}
