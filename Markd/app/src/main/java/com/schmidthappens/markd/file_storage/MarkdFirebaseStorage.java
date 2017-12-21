package com.schmidthappens.markd.file_storage;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.schmidthappens.markd.utilities.StringUtilities;

import java.io.File;
import java.io.InputStream;

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
            return false;
        }

        StorageReference storageReference = storage.getReference().child("images/" + path);
        if(storageReference.getDownloadUrl().isSuccessful()) {
            // Load the image using Glide
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(imageView);

            return true;
        } else {
            return false;
        }
    }
}
