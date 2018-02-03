package com.schmidthappens.markd.file_storage;

import com.google.firebase.storage.StorageMetadata;

/**
 * Created by joshua.schmidtibm.com on 2/3/18.
 */

public interface StorageMetadataListener {
    void onStart();
    void onSuccess(StorageMetadata metadata);
    void onFailed(Exception e);
}
