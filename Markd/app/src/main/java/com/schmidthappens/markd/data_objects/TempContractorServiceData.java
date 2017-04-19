package com.schmidthappens.markd.data_objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 4/19/2017.
 */

public class TempContractorServiceData {
    private static final TempContractorServiceData serviceData = new TempContractorServiceData();
    private List<ContractorService> hotWaterServices = new ArrayList<>();

    private TempContractorServiceData() {
        hotWaterServices.add(new ContractorService(8, 2, 16, "SDR Plumbing & Heating", "Routine Maintenance"));
        hotWaterServices.add(new ContractorService(1, 17, 14, "SDR Plumbing & Heating", "Installed new hot water heater"));

    }

    public static TempContractorServiceData getInstance() {
        return serviceData;
    }

    public List<ContractorService> getHotWaterServices() {
        return hotWaterServices;
    }
}
