package com.schmidthappens.markd.customer_menu_activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.customer_subactivities.HomeEditActivity;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.file_storage.DownloadUrlListener;
import com.schmidthappens.markd.file_storage.ImageLoadingListener;
import com.schmidthappens.markd.file_storage.MarkdFirebaseStorage;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.utilities.ProgressBarUtilities;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;

    //XML Objects
    private ProgressBar progressBar;
    private FrameLayout homeFrame;
    private ImageView homeImage;
    private ImageView homeImagePlaceholder;
    private TextView preparedFor;
    private TextView homeAddress;
    private TextView roomInformation;
    private TextView squareFootage;

    private static final int PLACE_HOLDER_IMAGE = R.drawable.ic_action_camera;
    private static HomeUrlListener downloadUrlListener;

    private boolean hasImage;
    private boolean cameraPermissionGranted;
    private static final int IMAGE_REQUEST_CODE = 524;
    private static final int CAMERA_PERMISSION_CODE = 99;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.menu_activity_home_view);

        authentication = new FirebaseAuthentication(this);
        new ActionBarInitializer(this, true, "customer");
        downloadUrlListener = new HomeUrlListener();
        initializeViews();
        checkForCameraPermission();
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        hasImage = false;

        customerData = new TempCustomerData(
                authentication.getCurrentUser().getUid(),
                new MainGetDataListener());
        homeFrame.setOnClickListener(photoClick);
        homeFrame.setOnLongClickListener(photoLongClick);
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraPermissionGranted = true;
            }
        }
    }

    // Mark:- SetUp Functions
    private void initializeViews() {
        progressBar = findViewById(R.id.home_image_progress);
        homeFrame = findViewById(R.id.home_frame);
        homeImage = findViewById(R.id.home_image);
        homeImagePlaceholder = findViewById(R.id.home_image_placeholder);

        homeFrame.setBackgroundColor(View.GONE);
        homeImage.setVisibility(View.GONE);
        homeImagePlaceholder.setVisibility(View.GONE);

        preparedFor = findViewById(R.id.prepared_for);
        homeAddress = findViewById(R.id.home_address);
        roomInformation = findViewById(R.id.home_information_rooms);
        squareFootage = findViewById(R.id.home_information_square_footage);


    }
    private void initializeUI() {
        fillCustomerInformation();
        setHomeImage();
    }
    private void fillCustomerInformation() {
        String preparedForString = "Prepared for " + customerData.getName();
        preparedFor.setText(preparedForString);

        final String address = customerData.getFormattedAddress();
        if(address == null) {
            Log.d(TAG, "Home information was null");
            startActivity(new Intent(MainActivity.this, HomeEditActivity.class));
            return;
        } else {
            homeAddress.setText(address);
        }

        final String homeInformationString = customerData.getRoomInformation();
        if(homeInformationString == null) {
            Log.d(TAG, "Home information was null");
            startActivity(new Intent(MainActivity.this, HomeEditActivity.class));
            return;
        } else {
            roomInformation.setText(homeInformationString);
        }

        final String squareFootageString = customerData.getSquareFootageString();
        if(squareFootageString == null) {
            Log.d(TAG, "Square Footage was null");
            startActivity(new Intent(MainActivity.this, HomeEditActivity.class));
        } else {
            squareFootage.setText(squareFootageString);
        }
    }
    private void setHomeImage() {
        Log.d(TAG, "Current FileName: " + customerData.getHomeImageFileName());
        MarkdFirebaseStorage.getDownloadUrl(
                customerData.getHomeImageFileName(), downloadUrlListener);
    }

    //Mark:- Photo Functions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == IMAGE_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                String oldFileName = customerData.getHomeImageFileName();
                Log.d(TAG, "oldFileName:" + oldFileName);
                String fileName = customerData.setHomeImageFileName();
                Log.d(TAG, "newFileName:" + fileName);

                Uri photo = getPhotoUri(data);

                if (photo != null) {
                    MarkdFirebaseStorage.updateImage(this, fileName, photo, downloadUrlListener);
                    Toast.makeText(this, "Updating Photo", Toast.LENGTH_LONG).show();
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

    private class MainGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() { }

        @Override
        public void onSuccess(DataSnapshot data) {
            initializeUI();
        }

        @Override
        public void onFailed(DatabaseError databaseError) { }
    }
    private class HomeUrlListener implements DownloadUrlListener {
        @Override
        public void onStart() {
            Log.i(TAG, "DownloadUrlListener: onStart");
            hasImage = false;
            homeFrame.setBackgroundColor(View.GONE);
            homeImage.setVisibility(View.GONE);
            homeImagePlaceholder.setVisibility(View.GONE);
            ProgressBarUtilities.showProgress(MainActivity.this, homeFrame, progressBar, true);
        }

        @Override
        public void onSuccess(final Uri url) {
            hasImage = true;
            Log.d(TAG, "DownloadUrlListener: onSuccess");

            homeFrame.setBackgroundColor(View.VISIBLE);
            homeImage.setVisibility(View.VISIBLE);

            homeFrame.setBackgroundColor(Color.TRANSPARENT);
            homeImage.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            Gravity.START));
            homeImagePlaceholder.setVisibility(View.GONE);
            Picasso.get()
                    .load(url)
                    .placeholder(PLACE_HOLDER_IMAGE)
                    .into(homeImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            ProgressBarUtilities.showProgress(MainActivity.this, homeFrame, progressBar, false);
                        }

                        @Override
                        public void onError(Exception e) {
                            ProgressBarUtilities.showProgress(MainActivity.this, homeFrame, progressBar, false);
                        }
                    });

        }

        @Override
        public void onCanceled() {
            Log.d(TAG, "DownloadUrlListener: onCanceled");
            ProgressBarUtilities.showProgress(MainActivity.this, homeFrame, progressBar, false);
        }

        @Override
        public void onFailed(final Exception e) {
            Log.d(TAG, "DownloadUrlListener: onFailed");

            hasImage = false;
            Log.e(TAG, e.toString());

            homeFrame.setBackgroundColor(View.VISIBLE);
            homeImage.setVisibility(View.GONE);

            homeFrame.setBackgroundColor(getResources().getColor(R.color.colorPanel));
            homeImagePlaceholder.setVisibility(View.VISIBLE);

            ProgressBarUtilities.showProgress(MainActivity.this, homeFrame, progressBar, false);
        }
    }
}
