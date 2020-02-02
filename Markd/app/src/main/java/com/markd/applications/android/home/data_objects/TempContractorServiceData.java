package com.markd.applications.android.home.data_objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 4/19/2017.
 */

public class TempContractorServiceData {
    private static final TempContractorServiceData serviceData = new TempContractorServiceData();
    private List<ContractorService> plumbingServices = new ArrayList<>();
    private List<ContractorService> electricalServices = new ArrayList<>();
    private List<ContractorService> hvacServices = new ArrayList<>();

    private TempContractorServiceData() {
        plumbingServices.add(new ContractorService(8, 2, 16, "SDR Plumbing & Heating", "Routine Maintenance", null));
        plumbingServices.add(new ContractorService(10, 28, 15, "SDR Plumbing & Heating", "Routine Maintenance", null));
        plumbingServices.add(new ContractorService(1, 17, 14, "SDR Plumbing & Heating", "Installed new hot water heater", null));
        plumbingServices.add(new ContractorService(3,13,14, "Bruni & Campsi", "Fixed broken pilot light", null));
        plumbingServices.add(new ContractorService(11, 7, 12, "Bruni & Campsi", "Installed new boiler", null));

        electricalServices.add(new ContractorService(10, 6, 16, "ConnWest Electric", "Installed exterior lighting", null));

        hvacServices.add(new ContractorService(1, 27, 14, "AireServ", "Installed Goodman Compressor",null));
    }

    public static TempContractorServiceData getInstance() {
        return serviceData;
    }
    public List<ContractorService> getPlumbingServices() {
        return plumbingServices;
    }
    public List<ContractorService> getElectricalServices() {
        return electricalServices;
    }
    public List<ContractorService> getHvacServices() {
        return hvacServices;
    }
}
