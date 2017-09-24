package com.schmidthappens.markd.data_objects;

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
        plumbingServices.add(new ContractorService(8, 2, 16, "SDR Plumbing & Heating", "Routine Maintenance"));
        plumbingServices.add(new ContractorService(10, 28, 15, "SDR Plumbing & Heating", "Routine Maintenance"));
        plumbingServices.add(new ContractorService(1, 17, 14, "SDR Plumbing & Heating", "Installed new hot water heater"));
        plumbingServices.add(new ContractorService(3,13,14, "Bruni & Campsi", "Fixed broken pilot light"));
        plumbingServices.add(new ContractorService(11, 7, 12, "Bruni & Campsi", "Installed new boiler"));

        electricalServices.add(new ContractorService(10, 6, 16, "ConnWest Electric", "Installed exterior lighting"));

        hvacServices.add(new ContractorService(1, 27, 14, "AireServ", "Installed Goodman Compressor"));
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

    private Contractor tempContractor = new Contractor("Greenwich Landscaping Company",  "2038691022", "http://greenwichlandscape.net/", "53532", "landscaper");
    public Contractor getContractor() {
        return tempContractor;
    }

}
