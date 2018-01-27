package com.schmidthappens.markd.file_storage;

import com.schmidthappens.markd.data_objects.ContractorService;
import com.schmidthappens.markd.utilities.StringUtilities;

/**
 * Created by joshua.schmidtibm.com on 1/27/18.
 */

public class ServiceImageStorageUtility {
    public static String getServiceImageFilePath(String customerUid, ContractorService service, String serviceImageFileName) {
        if(StringUtilities.isNullOrEmpty(customerUid) || service == null || StringUtilities.isNullOrEmpty(serviceImageFileName)) {
            return "";
        }
        String serviceDate = service.getUrlFormattedString();
        String serviceContractor = service.getContractor();
        if(StringUtilities.isNullOrEmpty(serviceDate) || StringUtilities.isNullOrEmpty(serviceContractor)) {
            return "";
        }
        return "services/" + customerUid + "/" + serviceContractor + "_" + serviceDate + "/" + serviceImageFileName;
    }
}
