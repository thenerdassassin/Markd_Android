package com.schmidthappens.markd.contractor_user_activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.TempContractorServiceData;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;

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

    //ActionBar
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

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

        SessionManager sessionManager = new SessionManager(ContractorMainActivity.this);
        sessionManager.checkLogin();

        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        logoFrame = (FrameLayout)findViewById(R.id.contractor_logo_frame);
        logoImage = (ImageView)findViewById(R.id.contractor_logo);
        logoImagePlaceholder = (ImageView)findViewById(R.id.contractor_logo_placeholder);

        companyNameTextView = (TextView)findViewById(R.id.contractor_company_name);
        companyTelephone = (TextView)findViewById(R.id.contractor_telephone_text);
        companyWebpage = (TextView)findViewById(R.id.contractor_website_textview);
        companyZipCode = (TextView)findViewById(R.id.contractor_zipcode_textview);

        //TODO: change to http call to get contractor
        TempContractorServiceData.Contractor contractor = TempContractorServiceData.getInstance().getContractor();
        initializeTextViews(contractor);

        //Set up ActionBar
        setUpActionBar();

        //Initialize DrawerList
        setUpDrawerToggle();
        NavigationDrawerInitializer ndi = new NavigationDrawerInitializer(this, drawerLayout, drawerList, drawerToggle, getResources().getStringArray(R.array.contractor_menu_options), getResources().getStringArray(R.array.contractor_menu_icons));
        ndi.setUp();

        //TODO change to only set as "0" if no image available from http call
        if(getLogoImageFile().exists()) {
            setPhoto(getLogoImageUri());
            logoImage.setTag("1");
        } else {
            //TODO: try and get photo from http call
            //if can't get then set to 0
            logoImage.setTag("0");
        }
        logoFrame.setOnClickListener(photoClick);
        logoFrame.setOnLongClickListener(photoLongClick);
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
            temp.delete();
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
        temp.delete();
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
            Context activityContext = ContractorMainActivity.this;
            Class destinationClass = ContractorEditActivity.class;
            Intent gotToContractorEditActivityIntent = new Intent(activityContext, destinationClass);
            addContractorDataToIntent(gotToContractorEditActivityIntent);
            startActivity(gotToContractorEditActivityIntent);
        }
    };
    private void addContractorDataToIntent(Intent intent) {
        //TODO: change to http call to get contractor
        TempContractorServiceData.Contractor contractor = TempContractorServiceData.getInstance().getContractor();
        intent.putExtra("companyName", contractor.getCompanyName());
        intent.putExtra("telephoneNumber", contractor.getTelephoneNumber());
        intent.putExtra("websiteUrl", contractor.getWebsiteUrl());
        intent.putExtra("zipCode", contractor.getZipCode());
    }

    // Mark:- SetUp Functions
    private void initializeTextViews(TempContractorServiceData.Contractor contractor) {
        companyNameTextView.setText(contractor.getCompanyName());
        companyTelephone.setText(contractor.getTelephoneNumber());
        companyWebpage.setText(contractor.getWebsiteUrl());
        companyZipCode.setText(contractor.getZipCode());
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.view_action_bar);
        //Set up actionBarButtons
        ImageView menuButton = (ImageView)findViewById(R.id.burger_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
            }
        });

        //Make edit mode accessible
        ImageView editButton = (ImageView)findViewById(R.id.edit_mode);
        editButton.setVisibility(View.VISIBLE);
        editButton.setClickable(true);
        editButton.setOnClickListener(editCompanyOnClickListener);
    }

    private void setUpDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }
}
