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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.TempContractorData;
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

    private static final int IMAGE_REQUEST_CODE = 1;
    private static final String filename = "contractor_logo.jpg";

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
    }

    //Mark:- Photo Functions
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO Save Image in Database

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if(copyFileToPermanentStorage()) {
                //Camera Results
                setPhoto(getLogoImageUri());
            } else {
                //Gallery Results
                Log.e(TAG, "Copy did not work");
                if(data.getData() != null) {
                    copyUriToPermanentStorage(data.getData());
                    setPhoto(data.getData());
                }
                return;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private boolean setPhoto(Uri uri) {
        logoFrame.setBackgroundColor(Color.TRANSPARENT);
        logoImage.setLayoutParams(
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.START)
        );

        logoImage.setImageURI(null); //work around to prevent caching
        logoImage.setImageURI(uri);
        logoImagePlaceholder.setVisibility(View.GONE);
        return true;
    }
    private Intent createPhotoOrGalleryChooserIntent() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile()));

        String pickTitle = "Take or select a photo";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        return chooserIntent;
    }
    private boolean copyFileToPermanentStorage() {
        File temp = getTempFile();
        if(!temp.exists()) {
            Log.e(TAG, "Temp file did not exist!");
            return false;
        }

        File logoImageFile = getLogoImageFile();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(temp);
            out = new FileOutputStream(logoImageFile);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            if(!temp.delete()) {
                Log.e(TAG, "Temp file was not deleted");
            }
            return true;
        } catch (Exception exception) {
            Log.e(TAG, exception.toString());
        }  finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch(IOException ioe) {
                //ignore
            }
        }
        if(!temp.delete()) {
            Log.e(TAG, "Temp file was not deleted");
        }
        return false;
    }
    private boolean copyUriToPermanentStorage(Uri uri) {
        File logoImageFile = getLogoImageFile();
        InputStream in = null;
        OutputStream out = null;

        try {
            in = getContentResolver().openInputStream(uri);
            out = new FileOutputStream(logoImageFile);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return true;
        } catch (Exception exception) {
            Log.e(TAG, exception.toString());
        }  finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch(IOException ioe) {
                //ignore
            }
        }
        return false;
    }
    private Uri getLogoImageUri() {
        return Uri.fromFile(getLogoImageFile());
    }
    private File getLogoImageFile() {
        return new File(ContractorMainActivity.this.getFilesDir(), filename);
    }
    private File getTempFile() {
        return new File(Environment.getExternalStorageDirectory(), "image.tmp");
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
            if(logoImage.getTag().equals("0")) {
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
        initializeTextViews(contractorData.getContractorDetails());
        //TODO change to only set as "0" if no image available from http call
        if(getLogoImageFile().exists()) {
            setPhoto(getLogoImageUri());
            logoImage.setTag("1");
        } else {
            //TODO: try and get photo from http call
            //if can't get then set to 0
            logoImage.setTag("0");
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
