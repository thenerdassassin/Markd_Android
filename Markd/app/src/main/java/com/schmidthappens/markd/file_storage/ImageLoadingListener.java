package com.schmidthappens.markd.file_storage;

/**
 * Created by joshua.schmidtibm.com on 12/21/17.
 */

public interface ImageLoadingListener {
    void onStart();
    void onSuccess();
    void onFailed(Exception e);
}
