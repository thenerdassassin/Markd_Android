package com.schmidthappens.markd.file_storage;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.schmidthappens.markd.utilities.DateUtitilities;
import com.schmidthappens.markd.utilities.StringUtilities;

/**
 * Created by joshua.schmidtibm.com on 12/19/17.
 */

public class MarkdFirebaseStorage {
    private static FirebaseStorage storage = FirebaseStorage.getInstance();

    public static UploadTask saveImage(String path, Uri file) {
        if(StringUtilities.isNullOrEmpty(path)) {
            return null;
        }
        StorageReference reference = storage.getReference().child("images/" + path);
        return reference.putFile(file);
    }

    public static boolean loadImage(Context context, String path, ImageView imageView) {
        if(StringUtilities.isNullOrEmpty(path)) {
            Log.d("Storage", "Not loading image");
            return false;
        }

        StorageReference storageReference = storage.getReference().child("images/" + path);
        Log.d("PATH:", path);
        //if(storageReference.getDownloadUrl().isSuccessful()) {
            Log.d("Storage", "Loading image");
            // Load the image using Glide
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(imageView);

            return true;
        /*} else {
            Log.d("Storage", "Not loading image");
            return false;
        }*/
    }

    public static void updateImage(final Context context, final String path, final Uri file, final ImageView imageView) {
        UploadTask uploadTask = saveImage(path, file);
        if(uploadTask != null) {
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    loadImage(context, path, imageView);
                }
            });
        }
    }

    public static void deleteImage(String path) {
        StorageReference reference = storage.getReference().child("images/" + path);
        reference.delete();
    }
}
