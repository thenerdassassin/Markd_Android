package com.schmidthappens.markd.file_storage;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.schmidthappens.markd.utilities.StringUtilities;

/**
 * Created by joshua.schmidtibm.com on 12/19/17.
 */

public class MarkdFirebaseStorage {
    private static final String TAG = "MarkdFirebaseStorage";
    private static FirebaseStorage storage = FirebaseStorage.getInstance();

    private static UploadTask saveImage(String path, Uri file) {
        if(StringUtilities.isNullOrEmpty(path)) {
            return null;
        }
        StorageReference reference = storage.getReference().child("images/" + path);
        return reference.putFile(file);
    }

    public static void loadImage(final Context context, String path, final ImageView imageView, final ImageLoadingListener listener) {
        if(listener != null) {
            listener.onStart();
        }
        if(StringUtilities.isNullOrEmpty(path)) {
            Log.d("Storage", "Not loading image");
            if(listener != null) {
                listener.onFailed(new IllegalArgumentException("Path is null or empty"));
            }
        }

        final StorageReference storageReference = storage.getReference().child("images/" + path);
        storageReference.getMetadata().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(listener != null) {
                    listener.onFailed(e);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                // Load the image using Glide
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(storageReference)
                        .into(imageView);
                if(listener != null) {
                    listener.onSuccess();
                }
            }
        });
    }

    public static void updateImage(final Context context, final String path, final Uri file, final ImageView imageView, final ImageLoadingListener listener) {
        if(listener != null) {
            listener.onStart();
        }
        UploadTask uploadTask = saveImage(path, file);
        if(uploadTask != null) {
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    loadImage(context, path, imageView, listener);
                }
            });
        }
    }

    public static void deleteImage(String path) {
        StorageReference reference = storage.getReference().child("images/" + path);
        reference.delete();
    }
}
