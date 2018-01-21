package com.schmidthappens.markd.customer_menu_activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.schmidthappens.markd.customer_subactivities.HomeEditActivity;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.file_storage.CustomerHomeImageStorageUtility;
import com.schmidthappens.markd.file_storage.ImageLoadingListener;
import com.schmidthappens.markd.file_storage.MarkdFirebaseStorage;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;

    //XML Objects
    private FrameLayout homeFrame;
    private ImageView homeImage;
    private ImageView homeImagePlaceholder;
    private TextView preparedFor;
    private TextView homeAddress;
    private TextView roomInformation;
    private TextView squareFootage;

    private TextView contactRealtor;
    private TextView contactArchitect;
    private TextView contactBuilder;

    private boolean hasImage;
    private static final int IMAGE_REQUEST_CODE = 524;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity_home_view);

        authentication = new FirebaseAuthentication(this);
        new ActionBarInitializer(this, true, "customer");
        initializeViews();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        hasImage = false;
        homeFrame.setBackgroundColor(View.GONE);
        homeImage.setVisibility(View.GONE);
        homeImagePlaceholder.setVisibility(View.GONE);

        customerData = new TempCustomerData((authentication.getCurrentUser().getUid()), new MainGetDataListener());
        homeFrame.setOnClickListener(photoClick);
        homeFrame.setOnLongClickListener(photoLongClick);
    }
    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }

    // Mark:- SetUp Functions
    private void initializeViews() {
        homeFrame = (FrameLayout)findViewById(R.id.home_frame);
        homeImage = (ImageView)findViewById(R.id.home_image);
        homeImagePlaceholder = (ImageView)findViewById(R.id.home_image_placeholder);
        preparedFor = (TextView)findViewById(R.id.prepared_for);
        homeAddress = (TextView)findViewById(R.id.home_address);
        roomInformation = (TextView)findViewById(R.id.home_information_rooms);
        squareFootage = (TextView)findViewById(R.id.home_information_square_footage);

        // TODO: decide if Coming Soon!
        contactRealtor = (TextView)findViewById(R.id.contact_realtor);
        contactRealtor.setOnClickListener(showContactAlertDialog);
        contactArchitect = (TextView)findViewById(R.id.contact_architect);
        contactArchitect.setOnClickListener(showContactAlertDialog);
        contactBuilder = (TextView)findViewById(R.id.contact_builder);
        contactBuilder.setOnClickListener(showContactAlertDialog);
    }
    private void initializeUI() {
        fillCustomerInformation();

        MarkdFirebaseStorage.loadImage(this,
                CustomerHomeImageStorageUtility.getHomeImageFilePath(customerData.getUid(), customerData.getHomeImageFileName()),
                homeImage,
                new HomeImageLoadingListener());
    }
    private void fillCustomerInformation() {
        String preparedForString = "Prepared for " + customerData.getName();
        preparedFor.setText(preparedForString);
        String address = customerData.getFormattedAddress();
        if(address == null) {
            Log.d(TAG, "Home information was null");
            startActivity(new Intent(MainActivity.this, HomeEditActivity.class));
            return;
        } else {
            homeAddress.setText(address);
        }
        String homeInformationString = customerData.getRoomInformation();
        if(homeInformationString == null) {
            Log.d(TAG, "Home information was null");
            startActivity(new Intent(MainActivity.this, HomeEditActivity.class));
        } else {
            roomInformation.setText(homeInformationString);
        }
        String squareFootageString = customerData.getSquareFootageString();
        if(squareFootageString == null) {
            Log.d(TAG, "Square Footage was null");
            startActivity(new Intent(MainActivity.this, HomeEditActivity.class));
        } else {
            squareFootage.setText(squareFootageString);
        }
    }

    //Mark:- Photo Functions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == IMAGE_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                String oldFileName = customerData.getHomeImageFileName();
                String oldPath = CustomerHomeImageStorageUtility.getHomeImageFilePath(customerData.getUid(), oldFileName);

                String fileName = customerData.setHomeImageFileName();
                String path = CustomerHomeImageStorageUtility.getHomeImageFilePath(customerData.getUid(), fileName);

                Uri photo = getPhotoUri(data);

                if (photo != null) {
                    MarkdFirebaseStorage.updateImage(this, path, photo, homeImage, new HomeImageLoadingListener());
                    MarkdFirebaseStorage.deleteImage(oldPath);
                    Toast.makeText(this, "Loading Photo", Toast.LENGTH_LONG).show();
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

        Intent cameraIntent = getCameraIntent();
        if(cameraIntent != null) {
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
            return chooserIntent;
        } else {
            return pickIntent;
        }
    }

    // Mark:- Camera Functions
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
                "home_image_" + UUID.randomUUID().toString(),  /* prefix */
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
            Intent chooserIntent = createPhotoOrGalleryChooserIntent();
            if (chooserIntent != null){
                startActivityForResult(chooserIntent, IMAGE_REQUEST_CODE);
            }
            return true;
        }
    };
    private View.OnClickListener photoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Only resets image if no image exists
            if(!hasImage) {
                Intent chooserIntent = createPhotoOrGalleryChooserIntent();
                if (chooserIntent != null){
                    startActivityForResult(chooserIntent, IMAGE_REQUEST_CODE);
                }
            }
        }
    };
    private View.OnClickListener showContactAlertDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String[] options = getContactArray(view);
            if(options != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Contact Realtor")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                if (which == 0) {
                                    Intent intent = new Intent()
                                            .setAction(Intent.ACTION_VIEW)
                                            .addCategory(Intent.CATEGORY_BROWSABLE)
                                            .setData(Uri.parse(options[0]));
                                    MainActivity.this.startActivity(intent);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_DIAL)
                                            .setData(Uri.parse("tel:" + options[1]));
                                    MainActivity.this.startActivity(intent);
                                }
                            }
                        }).create().show();
            }
        }
    };
    private String[] getContactArray(View view) {
        if(view.equals(contactRealtor)) {
            Log.i(TAG, "Contact Realtor");
            return realtor_contact_array;
        } else if(view.equals(contactArchitect)) {
            Log.i(TAG, "Contact Architect");
            return architect_contact_array;
        } else if(view.equals(contactBuilder)) {
            Log.i(TAG, "Contact Builder");
            return builder_contact_array;
        } else {
            Log.e(TAG, "Can't find match for contact");
            return null;
        }
    }

    private class MainGetDataListener implements OnGetDataListener {
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
    private class HomeImageLoadingListener implements ImageLoadingListener {
        @Override
        public void onStart() {
            Log.i(TAG, "Loading photo");
            hasImage = false;
            homeFrame.setBackgroundColor(View.GONE);
            homeImage.setVisibility(View.GONE);
            homeImagePlaceholder.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess() {
            hasImage = true;
            Log.d(TAG, "Got photo");

            homeFrame.setBackgroundColor(View.VISIBLE);
            homeImage.setVisibility(View.VISIBLE);

            homeFrame.setBackgroundColor(Color.TRANSPARENT);
            homeImage.setLayoutParams(
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.START)
            );
            homeImagePlaceholder.setVisibility(View.GONE);
        }

        @Override
        public void onFailed(Exception e) {
            hasImage = false;
            Log.e(TAG, e.toString());

            homeFrame.setBackgroundColor(View.VISIBLE);
            homeImage.setVisibility(View.GONE);

            homeFrame.setBackgroundColor(getResources().getColor(R.color.colorPanel));
            homeImagePlaceholder.setVisibility(View.VISIBLE);
        }
    }

    //TODO: change when realtor loads
    private String[] realtor_contact_array = {
            "http://www.realtorwebsite.com",
            "(920)428-8454"
    };
    //TODO: change when builder loads
    private String[] builder_contact_array = {
            "http://www.builderwebsite.com",
            "(920)428-8454"
    };
    //TODO: change when architect loads
    private String[] architect_contact_array = {
            "http://www.architectwebsite.com",
            "(920)428-8454"
    };
}
