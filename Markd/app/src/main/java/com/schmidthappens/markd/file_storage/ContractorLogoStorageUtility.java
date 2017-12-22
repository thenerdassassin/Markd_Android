package com.schmidthappens.markd.file_storage;

/**
 * Created by joshua.schmidtibm.com on 12/21/17.
 */

public class ContractorLogoStorageUtility {
    public static String getLogoPath(String contractorUid, String logoFileName) {
        return "logos/" + contractorUid + "/" + logoFileName;
    }
}
