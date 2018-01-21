package com.schmidthappens.markd.contractor_user_activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import com.schmidthappens.markd.file_storage.ContractorLogoStorageUtility;
import com.schmidthappens.markd.file_storage.ImageLoadingListener;
import com.schmidthappens.markd.file_storage.MarkdFirebaseStorage;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
    private boolean cameraPermissionGranted;
    private static final int IMAGE_REQUEST_CODE = 524;
    private static final int CAMERA_PERMISSION_CODE = 107;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contractor_main_view);
        authentication = new FirebaseAuthentication(this);
        new ActionBarInitializer(this, false, "contractor", editCompanyOnClickListener);
        initializeXmlObjects();
        checkForCameraPermission();
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
        hasImage = false;
        logoFrame.setBackgroundColor(View.GONE);
        logoImage.setVisibility(View.GONE);
        logoImagePlaceholder.setVisibility(View.GONE);
        contractorData = new TempContractorData((authentication.getCurrentUser().getUid()), new ContractorMainGetDataListener());
        logoFrame.setOnClickListener(photoClick);
        logoFrame.setOnLongClickListener(photoLongClick);
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
        if(contractorData != null) {
            contractorData.removeListener();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraPermissionGranted = true;
            }
        }
    }

    //Mark:- Photo Functions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == IMAGE_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                String oldFileName = contractorData.getLogoFileName();
                String oldPath = ContractorLogoStorageUtility.getLogoPath(authentication.getCurrentUser().getUid(), oldFileName);

                String fileName = contractorData.setLogoFileName();
                String path = ContractorLogoStorageUtility.getLogoPath(authentication.getCurrentUser().getUid(), fileName);

                Uri photo = getPhotoUri(data);

                if (photo != null) {
                    MarkdFirebaseStorage.updateImage(this, path, photo, logoImage, new LogoLoadingListener());
                    MarkdFirebaseStorage.deleteImage(oldPath);
                    Toast.makeText(this, "Updating Logo", Toast.LENGTH_LONG).show();
                }
            } else {
                    Log.d(TAG, "Result not okay");
            }
        } else {
                Log.e(TAG, "Unknown Request");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private Uri getPhotoUri(Intent data) {
        Uri photoUri = null;
        File tempImage = new File(currentPhotoPath);
        if(data != null) {
            Log.d(TAG, "Getting data from intent");
            photoUri = data.getData();
        }
        if (photoUri == null) {
            Log.d(TAG, "Getting Uri from tempImage file");
            photoUri = Uri.fromFile(tempImage);
        }
        return photoUri;
    }
    private Intent createPhotoOrGalleryChooserIntent() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");

        String pickTitle = "Take or select a photo";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);

        if(cameraPermissionGranted) {
            Intent cameraIntent = getCameraIntent();
            if(cameraIntent != null) {
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
                return chooserIntent;
            }
        }
        return pickIntent;
    }

    // Mark:- Camera Functions
    private void checkForCameraPermission() {
        cameraPermissionGranted = true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionGranted = false;
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE }, CAMERA_PERMISSION_CODE);
        }
    }
    private Intent getCameraIntent() {
        //Check if Camera Feature Exists
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    // Error occurred while creating the File
                    Log.e(TAG, e.toString());
                    return null;
                }
                // Continue only if the File was successfully created
                if (photoFile != null && photoFile.exists()) {
                    Log.d(TAG, "Using FileProvider");
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.schmidthappens.markd.provider",
                            photoFile);
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    return takePhotoIntent;
                } else {
                    Log.e(TAG, "photoFile not configured");
                    return null;
                }
            } else {
                Log.e(TAG, "ResolveActivity is null");
                return null;
            }
        } else {
            return null;
        }
    }
    private File createImageFile() throws IOException {
        File image = File.createTempFile(
                "logo_image_" + UUID.randomUUID().toString(),  /* prefix */
                ".jpg",         /* suffix */
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)      /* directory */
        );

        if(!image.getParentFile().exists()) {
            Log.d(TAG, "Parent File does not exist");
            if(image.getParentFile().mkdirs()) {
                Log.e(TAG, "mkdirs:true");
            }else {
                Log.e(TAG, "mkdirs:false");
            }
        } else {
            Log.d(TAG, "Parent File exists");
        }
        if(image.exists()) {
            Log.e(TAG, "Image exists");
            Log.e(TAG, "Path:"+image.getAbsolutePath());
        } else {
            Log.e(TAG, "Image does not exist");
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
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
            if(!hasImage) {
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
    private void initializeXmlObjects() {
        logoFrame = (FrameLayout)findViewById(R.id.contractor_logo_frame);
        logoImage = (ImageView)findViewById(R.id.contractor_logo);
        logoImagePlaceholder = (ImageView)findViewById(R.id.contractor_logo_placeholder);

        companyNameTextView = (TextView)findViewById(R.id.contractor_company_name);
        companyTelephone = (TextView)findViewById(R.id.contractor_telephone_text);
        companyWebpage = (TextView)findViewById(R.id.contractor_website_textview);
        companyZipCode = (TextView)findViewById(R.id.contractor_zipcode_textview);
    }
    private void initializeUI() {
        Log.d(TAG, contractorData.toString());
        initializeTextViews(contractorData.getContractorDetails());
        MarkdFirebaseStorage.loadImage(this,
                ContractorLogoStorageUtility.getLogoPath(authentication.getCurrentUser().getUid(), contractorData.getLogoFileName()),
                logoImage,
                new LogoLoadingListener());
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
            initializeUI();
        }

        @Override
        public void onFailed(DatabaseError databaseError) {
            Log.e(TAG, "Failed to get Data");
        }
    }
    private class LogoLoadingListener implements ImageLoadingListener {
        @Override
        public void onStart() {
            Log.i(TAG, "Loading photo");
            hasImage = false;
            logoFrame.setBackgroundColor(View.GONE);
            logoImage.setVisibility(View.GONE);
            logoImagePlaceholder.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess() {
            hasImage = true;
            Log.d(TAG, "Got photo");

            logoFrame.setBackgroundColor(View.VISIBLE);
            logoImage.setVisibility(View.VISIBLE);

            logoFrame.setBackgroundColor(Color.TRANSPARENT);
            logoImage.setLayoutParams(
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.START)
            );
            logoImagePlaceholder.setVisibility(View.GONE);
        }

        @Override
        public void onFailed(Exception e) {
            hasImage = false;
            Log.e(TAG, e.toString());

            logoFrame.setBackgroundColor(View.VISIBLE);
            logoImage.setVisibility(View.GONE);

            logoFrame.setBackgroundColor(getResources().getColor(R.color.colorPanel));
            logoImagePlaceholder.setVisibility(View.VISIBLE);
        }
    }
}
