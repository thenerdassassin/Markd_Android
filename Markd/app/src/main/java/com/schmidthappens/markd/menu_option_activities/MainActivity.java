package com.schmidthappens.markd.menu_option_activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.ViewInitializers.NavigationDrawerInitializer;

import java.io.File;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {
    //ActionBar
    private ActionBar actionBar;
    private ActionBarDrawerToggle drawerToggle;

    //NavigationDrawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    //XML Objects
    FrameLayout homeFrame;
    ImageView homeImage;
    ImageView homeImagePlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);
        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        homeFrame = (FrameLayout)findViewById(R.id.home_frame);
        homeImage = (ImageView)findViewById(R.id.home_image);
        homeImagePlaceholder = (ImageView) findViewById(R.id.home_image_placeholder);

        //Set up ActionBar
        setUpActionBar();

        //Initialize DrawerList
        setUpDrawerToggle();
        NavigationDrawerInitializer ndi = new NavigationDrawerInitializer(this, drawerLayout, drawerList, drawerToggle);
        ndi.setUp();

        //TODO change to only set as "0" if no image available from http call
        homeImage.setTag("0");
        homeFrame.setOnClickListener(photoClick);
        homeFrame.setOnLongClickListener(photoLongClick);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO Save Image in Database
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            } else {
                File picture = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
                Uri imgUri = Uri.fromFile(picture);
                homeImage.setImageURI(imgUri);

                try {
                    //Used for Gallery
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    homeFrame.setBackgroundColor(Color.TRANSPARENT);
                    homeImage.setLayoutParams(
                            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.START)
                    );
                    homeImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    //Used for Camera
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    homeImage.setImageBitmap(photo);
                }
                homeImage.setTag("1");
                homeImagePlaceholder.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // Mark:- Action Listeners
    private View.OnLongClickListener photoLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Intent pickIntent = new Intent();
            pickIntent.setType("image/*");
            pickIntent.setAction(Intent.ACTION_GET_CONTENT);
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String pickTitle = "Take or select a photo";
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
            startActivityForResult(chooserIntent, 1); //PHOTO_CAPTURE_CODE
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
                String pickTitle = "Take or select a photo";
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
                startActivityForResult(chooserIntent, 1); //PHOTO_CAPTURE_CODE
            }
        }
    };
    // Mark:- SetUp Functions
    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar);
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
