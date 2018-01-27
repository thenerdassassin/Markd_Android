package com.schmidthappens.markd.file_storage;

import com.schmidthappens.markd.utilities.StringUtilities;

/**
 * Created by joshua.schmidtibm.com on 12/21/17.
 */

public class ContractorLogoStorageUtility {
    public static String getLogoPath(String contractorUid, String logoFileName) {
        if(StringUtilities.isNullOrEmpty(contractorUid) || StringUtilities.isNullOrEmpty(logoFileName)) {
            return "";
        }
        return "logos/" + contractorUid + "/" + logoFileName;
    }
}
