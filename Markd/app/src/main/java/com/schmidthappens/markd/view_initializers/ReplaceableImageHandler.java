package com.schmidthappens.markd.view_initializers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.file_storage.ImageLoadingListener;
import com.schmidthappens.markd.file_storage.MarkdFirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by joshua.schmidtibm.com on 2/2/18.
 */

public class ReplaceableImageHandler {
    private final static String TAG = "ReplaceableImageInit";

    private Activity context;
    private FrameLayout imageFrame;
    private ImageView imageView;
    private ImageView imagePlaceholder;

    private boolean hasImage;
    private boolean cameraPermissionGranted;
    private String currentPhotoPath;

    private int imageRequestCode;

    public View inititialize(Activity context, boolean hasImage, boolean cameraPermissionGranted, int imageRequestCode) {
        this.context = context;
        this.hasImage = hasImage;
        this.cameraPermissionGranted = cameraPermissionGranted;
        this.imageRequestCode = imageRequestCode;

        LayoutInflater viewInflater = LayoutInflater.from(context);
        View view = viewInflater.inflate(R.layout.view_replaceable_image, null);

        imageFrame = view.findViewById(R.id.replaceable_image_frame);
        imageView = view.findViewById(R.id.replaceable_image);
        imagePlaceholder = view.findViewById(R.id.replaceable_image_placeholder);
        imageFrame.setOnClickListener(photoClick);
        imageFrame.setOnLongClickListener(photoLongClick);

        return view;
    }

    public void loadImage(String fileName) {
        Log.d(TAG, fileName);
        MarkdFirebaseStorage.loadImage(context,
                fileName,
                imageView,
                null);
    }

    public void updateImage(String oldFileName, String newFileName, Intent data, ImageLoadingListener listener) {
        Uri photo = getPhotoUri(data);

        if (photo != null) {
            MarkdFirebaseStorage.updateImage(context, newFileName, photo, imageView, listener);
            MarkdFirebaseStorage.deleteImage(oldFileName);
            Toast.makeText(context, "Loading Photo", Toast.LENGTH_LONG).show();
        }
    }

    public void setCameraPermission(boolean cameraPermissionGranted) {
        this.cameraPermissionGranted = cameraPermissionGranted;
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
    private Intent getCameraIntent() {
        //Check if Camera Feature Exists
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePhotoIntent.resolveActivity(context.getPackageManager()) != null) {
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
                    Uri photoURI = FileProvider.getUriForFile(context,
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
                "service_image_" + UUID.randomUUID().toString(),  /* prefix */
                ".jpg",         /* suffix */
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)      /* directory */
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
    private View.OnLongClickListener photoLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Intent chooserIntent = createPhotoOrGalleryChooserIntent();
            if (chooserIntent != null){
                context.startActivityForResult(chooserIntent, imageRequestCode);
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
                    context.startActivityForResult(chooserIntent, imageRequestCode);
                }
            }
        }
    };
}
