package com.schmidthappens.markd.data_objects;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.schmidthappens.markd.utilities.StringUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

@IgnoreExtraProperties
public class Customer {
    public final String userType = "customer";

    //For Home Page
    private String namePrefix;
    private String firstName;
    private String lastName;
    private String maritalStatus;
    private Address address;
    private Home home;
    private String architectReference;
    private String builderReference;
    private String realtorReference;
    private String homeImageFileName;

    //For Plumbing Page
    private HotWater hotWater;
    private Boiler boiler;
    private String plumberReference;
    private List<ContractorService> plumbingServices;

    //For HVAC Page
    private AirHandler airHandler;
    private Compressor compressor;
    private String hvactechnicianReference;
    private List<ContractorService> hvacServices;

    //For Electrical Page
    private List<Panel> panels;
    private String electricianReference;
    private List<ContractorService> electricalServices;

    //For Painting Page
    private List<PaintSurface> interiorPaintSurfaces;
    private List<PaintSurface> exteriorPaintSurfaces;
    private String painterReference;

    public Customer() {
        // Default constructor required for calls to DataSnapshot.getValue(Customer.class)
    }

    //Mark:- Getters/Setters
        //:- Home Page
        public String getNamePrefix() {
            return namePrefix;
        }
        public void setNamePrefix(String namePrefix) {
            this.namePrefix = namePrefix;
        }
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        public String getLastName() {
            return lastName;
        }
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getMaritalStatus() {
            return maritalStatus;
        }
        public void setMaritalStatus(String maritalStatus) {
            this.maritalStatus = maritalStatus;
        }
        public Home getHome() {
            return home;
        }
        public void setHome(Home home) {
            this.home = home;
        }
        public Address getAddress() {
            return address;
        }
        public void setAddress(Address address) {
            this.address = address;
        }

        public String getArchitectReference() {
            return architectReference;
        }
        public void setArchitect(String architect) {
            this.architectReference = architect;
        }
        public String getBuilder() {
            return builderReference;
        }
        public void setBuilder(String builder) {
            this.builderReference = builder;
        }
        public String getRealtor() {
            return realtorReference;
        }
        public void setRealtor(String realtor) {
            this.realtorReference = realtor;
        }

        public String getHomeImageFileName() {
            return homeImageFileName;
        }
        public Customer setHomeImageFileName() {
            this.homeImageFileName = UUID.randomUUID().toString();
            return this;
        }

        //:- Plumbing Page
        public HotWater getHotWater() {
            return hotWater;
        }
        public void setHotWater(HotWater hotWater) {
            this.hotWater = hotWater;
        }
        public Boiler getBoiler() {
            return boiler;
        }
        public void setBoiler(Boiler boiler) {
            this.boiler = boiler;
        }
        public String getPlumberReference() {
            return plumberReference;
        }
        public void setPlumber(String plumber) {
            this.plumberReference = plumber;
        }
        public List<ContractorService> getPlumbingServices() {
            return plumbingServices;
        }
        public void setPlumbingServices(List<ContractorService> plumbingServices) {
            this.plumbingServices = plumbingServices;
        }
        @Exclude
        public void addPlumbingService(ContractorService service) {
            setPlumbingServices(setService(getPlumbingServices(), -1, service));
        }
        @Exclude
        public void updatePlumbingService(int serviceId, String company, String description) {
            List<ContractorService> services = getPlumbingServices();
            ContractorService service = services.get(serviceId);
            service.setContractor(company);
            service.setComments(description);
            setService(services, serviceId, service);
        }
        @Exclude
        public void deletePlumbingService(int serviceId) {
            setPlumbingServices(deleteService(getPlumbingServices(), serviceId));
        }

        //:- HVAC Page
        public AirHandler getAirHandler() {
            return airHandler;
        }
        public void setAirHandler(AirHandler airHandler) {
            this.airHandler = airHandler;
        }
        public Compressor getCompressor() {
            return compressor;
        }
        public void setCompressor(Compressor compressor) {
            this.compressor = compressor;
        }
        public String getHvactechnicianReference() {
            return hvactechnicianReference;
        }
        public void setHvactechnician(String hvactechnician) {
            this.hvactechnicianReference = hvactechnician;
        }
        public List<ContractorService> getHvacServices() {
            return hvacServices;
        }
        public void setHvacServices(List<ContractorService> hvacServices) {
            this.hvacServices = hvacServices;
        }
        @Exclude
        public void addHvacService(ContractorService service) {
            setHvacServices(setService(getHvacServices(), -1, service));
        }
        @Exclude
        public void updateHvacService(int serviceId, String company, String description) {
            List<ContractorService> services = getHvacServices();
            ContractorService service = services.get(serviceId);
            service.setContractor(company);
            service.setComments(description);
            setService(services, serviceId, service);
        }
        @Exclude
        public void deleteHvacService(int serviceId) {
            setHvacServices(deleteService(getHvacServices(), serviceId));
        }

        //:- Painting Page
        public List<PaintSurface> getExteriorPaintSurfaces() {
        return exteriorPaintSurfaces;
    }
        public void setExteriorPaintSurfaces(List<PaintSurface> surfaces) {
            this.exteriorPaintSurfaces = surfaces;
        }
        public List<PaintSurface> getInteriorPaintSurfaces() {
            return interiorPaintSurfaces;
        }
        public void setInteriorPaintSurfaces(List<PaintSurface> surfaces) {
            this.interiorPaintSurfaces = surfaces;
        }
        public String getPainterReference() {
            return painterReference;
        }
        public void setPainter(String painter) {
            this.painterReference = painter;
        }

        //:- Electrical Page
        public List<Panel> getPanels() {
            return panels;
        }
        public void setPanels(List<Panel> panels) {
            this.panels = panels;
        }
        public String getElectricianReference() {
            return electricianReference;
        }
        public void setElectricianReference(String electrician) {
            this.electricianReference = electrician;
        }
        public List<ContractorService> getElectricalServices() {
            return electricalServices;
        }
        public void setElectricalServices(List<ContractorService> electricalServices) {
            this.electricalServices = electricalServices;
        }
        @Exclude
        public void addElectricalService(ContractorService service) {
            setElectricalServices(setService(getElectricalServices(), -1, service));

        }
        @Exclude
        public void updateElectricalService(int serviceId, String company, String description) {
            List<ContractorService> services = getElectricalServices();
            ContractorService service = services.get(serviceId);
            service.setContractor(company);
            service.setComments(description);
            setService(services, serviceId, service);
        }
        @Exclude
        public void deleteElectricalService(int serviceId) {
            setElectricalServices(deleteService(getElectricalServices(), serviceId));
        }

    //Mark:- Helper functions
    public void updateProfile(String namePrefix, String firstName, String lastName, String maritalStatus) {
        this.namePrefix = namePrefix;
        this.firstName = firstName;
        this.lastName = lastName;
        this.maritalStatus = maritalStatus;
    }
    public void updateHome(String street, String city, String state, String zipcode, Double bedrooms, Double bathrooms, Integer squareFootage) {
        this.address = new Address(street, city, state, zipcode);
        this.home = new Home(bedrooms, bathrooms, squareFootage);
    }
    @Exclude
    public String getName() {
        return StringUtilities.getFormattedName(namePrefix, firstName, lastName, maritalStatus);
    }
    @Exclude
    private List<ContractorService> setService(List<ContractorService> services, int serviceId, ContractorService service) {
        if(serviceId == -1) {
            if(services == null) {
                services = new ArrayList<>();
            }
            services.add(0, service);
        } else {
            services.set(serviceId, service);
        }
        return services;
    }
    @Exclude
    private List<ContractorService> deleteService(List<ContractorService> services, int serviceId) {
        if(services != null && serviceId >= 0 && serviceId < services.size()) {
            services.remove(serviceId);
        }
        return services;
    }
    //Painting
    @Exclude
    public void setExteriorPaintSurface(int paintId, PaintSurface surface) {
        if(paintId == -1) {
            if(exteriorPaintSurfaces == null) {
                exteriorPaintSurfaces = new ArrayList<>();
            }
            exteriorPaintSurfaces.add(surface);
        } else {
            exteriorPaintSurfaces.set(paintId, surface);
        }
    }
    @Exclude
    public void setInteriorPaintSurface(int paintId, PaintSurface surface) {
        if(paintId == -1) {
            if(interiorPaintSurfaces == null) {
                interiorPaintSurfaces = new ArrayList<>();
            }
            interiorPaintSurfaces.add(surface);
        } else {
            interiorPaintSurfaces.set(paintId, surface);
        }
    }
    public void deleteExteriorPaintSurface(int paintId){
        exteriorPaintSurfaces.remove(paintId);
    }
    public void deleteInteriorPaintSurface(int paintId) {
        interiorPaintSurfaces.remove(paintId);
    }
    //Electrical
    @Exclude
    public void setPanel(int panelId, Panel updatedPanel) {
        if(panelId == -1) {
            if(panels == null) {
                panels = new ArrayList<>();
            }
            panels.add(updatedPanel);
        } else {
            panels.set(panelId, updatedPanel);
        }
    }
    public void deletePanel(int panelId) {
        panels.remove(panelId);
    }
}
