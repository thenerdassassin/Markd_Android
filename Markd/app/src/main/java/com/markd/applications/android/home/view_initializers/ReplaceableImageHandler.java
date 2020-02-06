package com.markd.applications.android.home.view_initializers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.firebase.storage.StorageMetadata;
import com.markd.applications.android.home.R;
import com.markd.applications.android.home.file_storage.DownloadUrlListener;
import com.markd.applications.android.home.file_storage.ImageLoadingListener;
import com.markd.applications.android.home.file_storage.MarkdFirebaseStorage;
import com.markd.applications.android.home.file_storage.StorageMetadataListener;
import com.markd.applications.android.home.utilities.ProgressBarUtilities;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Josh Schmidt on 2/2/18.
 */

public class ReplaceableImageHandler {
    private final static String TAG = "ReplaceableImageInit";

    private Activity context;
    private FrameLayout imageFrame;
    private FrameLayout imageCaptureFrame;
    private ImageView imageView;
    private ImageView imagePlaceholder;
    private Button viewPdf;

    private boolean hasImage;
    private boolean cameraPermissionGranted;
    private String currentPhotoPath;
    private String firebasePath;

    private int imageRequestCode;
    private ProgressBar progressBar;
    private LinearLayout layoutToHide;

    public View initialize(
            final Activity context,
            boolean hasImage,
            boolean cameraPermissionGranted,
            int imageRequestCode,
            final ProgressBar progressBar,
            final LinearLayout layoutToHide) {
        this.context = context;
        this.hasImage = hasImage;
        this.cameraPermissionGranted = cameraPermissionGranted;
        this.imageRequestCode = imageRequestCode;
        this.progressBar = progressBar;
        this.layoutToHide = layoutToHide;

        LayoutInflater viewInflater = LayoutInflater.from(context);
        View view = viewInflater.inflate(R.layout.view_replaceable_image, null);

        imageFrame = view.findViewById(R.id.replaceable_image_frame);
        imageCaptureFrame = view.findViewById(R.id.image_capture);
        imageView = view.findViewById(R.id.replaceable_image);
        imagePlaceholder = view.findViewById(R.id.replaceable_image_placeholder);
        imageFrame.setOnClickListener(photoClick);
        imageFrame.setOnLongClickListener(photoLongClick);
        viewPdf = view.findViewById(R.id.view_pdf_file_button);
        viewPdf.setOnClickListener(viewPdfClick);

        return view;
    }

    public void loadImage(String fileName) {
        Log.d(TAG, "Load:" + fileName);
        this.firebasePath = fileName;
        MarkdFirebaseStorage.getFileType(fileName, new StorageMetadataGetter(fileName));
    }
    public void updateImage(String newFileName, Intent data) {
        Uri photo = getPhotoUri(data);

        if (photo != null) {
            Log.d(TAG, "Saving to - " + newFileName);
            MarkdFirebaseStorage.saveImage(
                    newFileName,
                    photo,
                    context.getContentResolver(),
                    new SaveFileListener(newFileName));
        } else {
            Log.d(TAG, "photo is null");
        }
    }

    public void grantCameraPermission() {
        this.cameraPermissionGranted = true;
    }
    private Intent createPhotoOrGalleryChooserIntent() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");

        String pickTitle = "Take or select a photo";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        List<Intent> extras = new ArrayList<>();
        if(cameraPermissionGranted) {
            Intent cameraIntent = getCameraIntent();
            if(cameraIntent != null) {
                Log.d(TAG, "camera intent added");
                extras.add(cameraIntent);
            }
        }
        Intent intentPDF = getPdfIntent();
        if(intentPDF != null) {
            Log.d(TAG, "intent pdf added");
            extras.add(intentPDF);
        }

        if(extras.size() > 0) {
            Intent[] intents = new Intent[extras.size()];
            intents = extras.toArray(intents);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
            return chooserIntent;
        } else {
            return pickIntent;
        }
    }
    private Intent getPdfIntent() {
        Intent intentPDF = new Intent(Intent.ACTION_GET_CONTENT);
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE);
        intentPDF.setType("application/pdf");
        return intentPDF;
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
                            "com.markd.applications.android.home.provider",
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
            return false;
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


    private class StorageMetadataGetter implements StorageMetadataListener {
        private String fileName;

        StorageMetadataGetter(String fileName) {
            this.fileName = fileName;
        }
        @Override
        public void onStart() {
            Log.d(TAG, "Getting metadata");
            ProgressBarUtilities.showProgress(context, layoutToHide, progressBar, true);
        }

        @Override
        public void onSuccess(StorageMetadata metadata) {
            String content = metadata.getContentType();
            if("application/pdf".equalsIgnoreCase(content)) {
                Log.d(TAG, "Type PDF");
                //Show view pdf button
                imageCaptureFrame.setVisibility(View.GONE);
                viewPdf.setVisibility(View.VISIBLE);
                ProgressBarUtilities.showProgress(context, layoutToHide, progressBar, false);
            } else {
                Log.d(TAG, "Type not PDF");
                imageCaptureFrame.setVisibility(View.VISIBLE);
                viewPdf.setVisibility(View.GONE);
                MarkdFirebaseStorage.getDownloadUrl(fileName, new ImageUrlListener());
            }
        }

        @Override
        public void onFailed(Exception e) {
            Log.e(TAG, e.toString());
            ProgressBarUtilities.showProgress(context, layoutToHide, progressBar, false);
        }
    }
    private class SaveFileListener implements ImageLoadingListener {
        private String fileName;

        SaveFileListener(String fileName) {
            this.fileName = fileName;
            firebasePath = fileName;
        }
        @Override
        public void onStart() {
            Log.d(TAG, "Saving image.");
            ProgressBarUtilities.showProgress(context, layoutToHide, progressBar, true);
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "Image Saved successfully");
            hasImage = true;
            loadImage(fileName);
        }

        @Override
        public void onFailed(Exception e) {
            Log.e(TAG, e.toString());
            ProgressBarUtilities.showProgress(context, layoutToHide, progressBar, false);
            Toast.makeText(context, "Oops...something went wrong.", Toast.LENGTH_SHORT).show();

        }
    }
    private class ImageUrlListener implements DownloadUrlListener {
        @Override
        public void onStart() {
            Log.i(TAG, "DownloadUrlListener: onStart");
            hasImage = false;
            imageCaptureFrame.setBackgroundColor(View.GONE);
            imageView.setVisibility(View.GONE);
            imagePlaceholder.setVisibility(View.GONE);
            ProgressBarUtilities.showProgress(context, layoutToHide, progressBar, true);
        }

        @Override
        public void onSuccess(final Uri url) {
            hasImage = true;
            Log.d(TAG, "DownloadUrlListener: onSuccess");

            imageCaptureFrame.setBackgroundColor(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);

            imageCaptureFrame.setBackgroundColor(Color.TRANSPARENT);
            imageView.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            Gravity.START));
            imagePlaceholder.setVisibility(View.GONE);

            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.ic_action_camera)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Successfully loaded image");
                            ProgressBarUtilities.showProgress(context, layoutToHide, progressBar, false);
                        }

                        @Override
                        public void onError(final Exception e) {
                            Log.e(TAG, e.getMessage());
                            ProgressBarUtilities.showProgress(context, layoutToHide, progressBar, false);
                        }
                    });

        }

        @Override
        public void onCanceled() {
            Log.d(TAG, "DownloadUrlListener: onCanceled");
            ProgressBarUtilities.showProgress(context, layoutToHide, progressBar, false);
        }

        @Override
        public void onFailed(final Exception e) {
            Log.d(TAG, "DownloadUrlListener: onFailed");

            hasImage = false;
            Log.e(TAG, e.toString());

            imageCaptureFrame.setBackgroundColor(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            imageCaptureFrame.setBackgroundColor(context.getResources().getColor(R.color.colorPanel));
            imagePlaceholder.setVisibility(View.VISIBLE);

            ProgressBarUtilities.showProgress(context, layoutToHide, progressBar, false);
        }
    }

    private static File createPdfFile(Activity context) throws IOException {
        File pdf = File.createTempFile(
                "service_pdf_" + UUID.randomUUID().toString(),  /* prefix */
                ".pdf",         /* suffix */
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)      /* directory */
        );

        if(!pdf.getParentFile().exists()) {
            Log.d(TAG, "Parent File does not exist");
            if(pdf.getParentFile().mkdirs()) {
                Log.e(TAG, "mkdirs:true");
            }else {
                Log.e(TAG, "mkdirs:false");
            }
        } else {
            Log.d(TAG, "Parent File exists");
        }
        if(pdf.exists()) {
            Log.e(TAG, "Image exists");
            Log.e(TAG, "Path:" + pdf.getAbsolutePath());
        } else {
            Log.e(TAG, "Image does not exist");
        }

        return pdf;
    }
    private static void deletePdfFile(File pdf) {
        pdf.delete();
    }

    private View.OnClickListener viewPdfClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(firebasePath == null) {
                return;
            }
            File localFile = null;
            try {
                localFile = createPdfFile(context);
                final Uri pdfUri = FileProvider.getUriForFile(
                        context, "com.markd.applications.android.home.provider", localFile
                );
                Log.d(TAG, "PDF file: " + localFile.getAbsolutePath());
                MarkdFirebaseStorage.getFile(firebasePath, localFile, taskSnapshot -> {
                    // Local temp file has been created
                    final Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(pdfUri, "application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    target.addFlags(
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(Intent.createChooser(target, "Open File"));
                    } catch (final ActivityNotFoundException e) {
                        Toast.makeText(context, "Download a pdf viewer from the app store.", Toast.LENGTH_SHORT).show();
                    } catch (final Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }, exception -> {
                    // Handle any errors
                    Log.e(TAG, exception.toString());
                });
            } catch (final IOException e) {
                Log.e(TAG, e.toString());
            } finally {
                if (localFile != null) {
                    deletePdfFile(localFile);
                }
            }
        }
    };
}