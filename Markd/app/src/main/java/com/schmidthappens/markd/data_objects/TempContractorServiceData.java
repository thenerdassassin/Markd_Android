package com.schmidthappens.markd.data_objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 4/19/2017.
 */

public class TempContractorServiceData {
    private static final TempContractorServiceData serviceData = new TempContractorServiceData();
    private List<ContractorService> hotWaterServices = new ArrayList<>();
    private List<ContractorService> boilerServices = new ArrayList<>();
    private List<ContractorService> electricalServices = new ArrayList<>();
    private List<ContractorService> compressorServices = new ArrayList<>();
    private List<ContractorService> airHandlerServices = new ArrayList<>();

    private TempContractorServiceData() {
        hotWaterServices.add(new ContractorService(8, 2, 16, "SDR Plumbing & Heating", "Routine Maintenance"));
        hotWaterServices.add(new ContractorService(1, 17, 14, "SDR Plumbing & Heating", "Installed new hot water heater"));

        boilerServices.add(new ContractorService(10, 28, 15, "SDR Plumbing & Heating", "Routine Maintenance"));
        boilerServices.add(new ContractorService(3,13,14, "Bruni & Campsi", "Fixed broken pilot light"));
        boilerServices.add(new ContractorService(11, 7, 12, "Bruni & Campsi", "Installed new boiler"));

        electricalServices.add(new ContractorService(10, 6, 16, "ConnWest Electric", "Installed exterior lighting"));

        compressorServices.add(new ContractorService(1, 27, 14, "AireServ", "Installed Goodman Compressor"));
    }

    public static TempContractorServiceData getInstance() {
        return serviceData;
    }
    public List<ContractorService> getHotWaterServices() {
        return hotWaterServices;
    }
    public List<ContractorService> getBoilerServices() {
        return boilerServices;
    }
    public List<ContractorService> getElectricalServices() {
        return electricalServices;
    }
    public List<ContractorService> getCompressorServices() {
        return compressorServices;
    }
    public List<ContractorService> getAirHandlerServices() {
        return airHandlerServices;
    }
}
