package com.markd.applications.android.home.file_storage;

import android.net.Uri;

public interface DownloadUrlListener {
    void onStart();
    void onSuccess(final Uri downloadUrl);
    void onCanceled();
    void onFailed(final Exception e);
}