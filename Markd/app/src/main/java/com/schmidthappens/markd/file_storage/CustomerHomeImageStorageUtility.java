package com.schmidthappens.markd.file_storage;

/**
 * Created by joshua.schmidtibm.com on 12/26/17.
 */

public class CustomerHomeImageStorageUtility {
    public static String getHomeImageFilePath(String customerUid, String homeImageFileName) {
        return "homes/" + customerUid + "/" + homeImageFileName;
    }
}
