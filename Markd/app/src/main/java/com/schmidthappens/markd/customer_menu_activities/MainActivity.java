package com.schmidthappens.markd.customer_menu_activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.customer_subactivities.HomeEditActivity;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.utilities.OnGetDataListener;
import com.schmidthappens.markd.view_initializers.ActionBarInitializer;
import com.schmidthappens.markd.view_initializers.NavigationDrawerInitializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;


public class MainActivity extends AppCompatActivity {
    //XML Objects
    private FrameLayout homeFrame;
    private ImageView homeImage;
    private ImageView homeImagePlaceholder;
    private TextView preparedFor;
    private TextView homeAddress;
    private TextView homeInformation;

    private TextView contactRealtor;
    private TextView contactArchitect;
    private TextView contactBuilder;

    private static final int IMAGE_REQUEST_CODE = 1;
    private static final String filename = "main_photo.jpg";

    private String TAG = "MainActivity";
    private FirebaseAuthentication authentication;
    TempCustomerData customerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity_home_view);

        authentication = new FirebaseAuthentication(this);
        new ActionBarInitializer(this, true, "customer");

        homeFrame = (FrameLayout)findViewById(R.id.home_frame);
        homeImage = (ImageView)findViewById(R.id.home_image);
        homeImagePlaceholder = (ImageView)findViewById(R.id.home_image_placeholder);
        preparedFor = (TextView)findViewById(R.id.prepared_for);
        homeAddress = (TextView)findViewById(R.id.home_address);
        homeInformation = (TextView)findViewById(R.id.home_information);

        contactRealtor = (TextView)findViewById(R.id.contact_realtor);
        contactRealtor.setOnClickListener(showContactAlertDialog);
        contactArchitect = (TextView)findViewById(R.id.contact_architect);
        contactArchitect.setOnClickListener(showContactAlertDialog);
        contactBuilder = (TextView)findViewById(R.id.contact_builder);
        contactBuilder.setOnClickListener(showContactAlertDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        customerData = new TempCustomerData((authentication.getCurrentUser().getUid()), new MainGetDataListener());
        homeFrame.setOnClickListener(photoClick);
        homeFrame.setOnLongClickListener(photoLongClick);
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
                setPhoto(getHomeImageUri());
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
        homeFrame.setBackgroundColor(Color.TRANSPARENT);
        homeImage.setLayoutParams(
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.START)
        );

        homeImage.setImageURI(null); //work around to prevent caching
        homeImage.setImageURI(uri);
        homeImagePlaceholder.setVisibility(View.GONE);
        return true;
    }

    private boolean copyFileToPermanentStorage() {
        File temp = getTempFile();
        if(!temp.exists()) {
            Log.e(TAG, "Temp file did not exist!");
            return false;
        }

        File homeImageFile = getHomeImageFile();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(temp);
            out = new FileOutputStream(homeImageFile);
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
        File homeImageFile = getHomeImageFile();
        InputStream in = null;
        OutputStream out = null;

        try {
            in = getContentResolver().openInputStream(uri);
            out = new FileOutputStream(homeImageFile);
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

    private Uri getHomeImageUri() {
        return Uri.fromFile(getHomeImageFile());
    }
    private File getHomeImageFile() {
        return new File(MainActivity.this.getFilesDir(), filename);
    }
    private File getTempFile() {
        return new File(Environment.getExternalStorageDirectory(), "image.tmp");
    }

    // Mark:- Action Listeners
    private View.OnLongClickListener photoLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Intent pickIntent = new Intent();
            pickIntent.setType("image/*");
            pickIntent.setAction(Intent.ACTION_GET_CONTENT);

            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(getTempFile()));

            String pickTitle = "Take or select a photo";

            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
            startActivityForResult(chooserIntent, IMAGE_REQUEST_CODE);

            return true;
        }
    };

    private View.OnClickListener photoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Only resets image if no image exists
            if(homeImage.getTag().equals("0")) {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhotoIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile()));

                String pickTitle = "Take or select a photo";
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
                startActivityForResult(takePhotoIntent, IMAGE_REQUEST_CODE);
            }
        }
    };

    private View.OnClickListener showContactAlertDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String[] contactArray = {};
            if(view.equals(contactRealtor)) {
                Log.i(TAG, "Contact Realtor");
                contactArray = realtor_contact_array;
            } else if(view.equals(contactArchitect)) {
                Log.i(TAG, "Contact Architect");
                contactArray = architect_contact_array;
            } else if(view.equals(contactBuilder)) {
                Log.i(TAG, "Contact Builder");
                contactArray = builder_contact_array;
            } else {
                Log.e(TAG, "Can't find match for contact");
            }
            final String[] options = contactArray;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Contact Realtor")
                    .setItems(options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            if(which == 0) {
                                Intent intent = new Intent()
                                        .setAction(Intent.ACTION_VIEW)
                                        .addCategory(Intent.CATEGORY_BROWSABLE)
                                        .setData(Uri.parse(options[0]));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_DIAL)
                                        .setData(Uri.parse("tel:" + options[1]));
                                startActivity(intent);
                            }
                        }
                    }).create().show();

        }
    };

    // Mark:- SetUp Functions
    private void initalizeUI() {
        initializeViews();

        //TODO change to only set as "0" if no image available from http call
        if(getHomeImageFile().exists()) {
            setPhoto(getHomeImageUri());
            homeImage.setTag("1");
        } else {
            //TODO: try and get photo from http call
            //if can't get then set to 0
            homeImage.setTag("0");
        }
    }
    private void initializeViews() {
        String preparedForString = "Prepared for " + customerData.getName();
        preparedFor.setText(preparedForString);
        if(customerData.getFormattedAddress() == null) {
            Log.d(TAG, "Home information was null");
            startActivity(new Intent(MainActivity.this, HomeEditActivity.class));
            return;
        } else {

            homeAddress.setText(customerData.getFormattedAddress());
        }
        String homeInformationString = customerData.getHomeInformation();
        if(homeInformationString == null) {
            Log.d(TAG, "Home information was null");
            startActivity(new Intent(MainActivity.this, HomeEditActivity.class));
            return;
        } else {
            homeInformation.setText(homeInformationString);
        }
    }

    private class MainGetDataListener implements OnGetDataListener {
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(DataSnapshot data) {
            initalizeUI();
        }

        @Override
        public void onFailed(DatabaseError databaseError) {

        }
    }

    //TODO: change when realtor loads
    private String[] realtor_contact_array = {
            "www.realtorwebsite.com",
            "(920)428-8454"
    };
    //TODO: change when builder loads
    private String[] builder_contact_array = {
            "www.builderwebsite.com",
            "(920)428-8454"
    };
    //TODO: change when architect loads
    private String[] architect_contact_array = {
            "www.architectwebsite.com",
            "(920)428-8454"
    };
}
