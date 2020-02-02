package com.markd.applications.android.home.file_storage;

/**
 * Created by Josh Schmidt on 12/21/17.
 */

public interface ImageLoadingListener {
    void onStart();
    void onSuccess();
    void onFailed(Exception e);
}
