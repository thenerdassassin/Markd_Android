package com.schmidthappens.markd.data_objects;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.schmidthappens.markd.utilities.StringUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    private String nameSuffix;
    private MaritalStatus maritalStatus;
    private Address address;
    private Home home;
    private ContractorDetails architect;
    private ContractorDetails builder;
    private ContractorDetails realtor;

    //For Plumbing Page
    private HotWater hotWater;
    private Boiler boiler;
    private ContractorDetails plumber;
    private List<ContractorService> plumbingServices;

    //For HVAC Page
    private AirHandler airHandler;
    private Compressor compressor;
    private ContractorDetails hvactechnician;
    private List<ContractorService> hvacServices;

    //For Electrical Page
    private List<Panel> panels;
    private ContractorDetails electrician;
    private List<ContractorService> electricalServices;

    //For Painting Page
    private List<PaintSurface> interiorPaintSurfaces;
    private List<PaintSurface> exteriorPaintSurfaces;
    private ContractorDetails painter;

    public Customer() {
        // Default constructor required for calls to DataSnapshot.getValue(Customer.class)
    }
    private Customer(String namePrefix, String firstName, String lastName, String nameSuffix, MaritalStatus maritalStatus, Address address, Home home, ContractorDetails architect, ContractorDetails builder, HotWater hotWater, Boiler boiler, ContractorDetails plumber, List<ContractorService> plumbingServices, AirHandler airHandler, Compressor compressor, ContractorDetails hvactechnician, List<ContractorService> hvacServices, List<Panel> panels, ContractorDetails electrician, List<ContractorService> electricalServices, List<PaintSurface> interiorPaintSurfaces, List<PaintSurface> exteriorPaintSurfaces, ContractorDetails painter) {
        this.namePrefix = namePrefix;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nameSuffix = nameSuffix;
        this.maritalStatus = maritalStatus;
        this.address = address;
        this.home = home;
        this.architect = architect;
        this.builder = builder;
        this.hotWater = hotWater;
        this.boiler = boiler;
        this.plumber = plumber;
        this.plumbingServices = plumbingServices;
        this.airHandler = airHandler;
        this.compressor = compressor;
        this.hvactechnician = hvactechnician;
        this.hvacServices = hvacServices;
        this.panels = panels;
        this.electrician = electrician;
        this.electricalServices = electricalServices;
        this.interiorPaintSurfaces = interiorPaintSurfaces;
        this.exteriorPaintSurfaces = exteriorPaintSurfaces;
        this.painter = painter;
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
        public String getNameSuffix() {
            return nameSuffix;
        }
        public void setNameSuffix(String nameSuffix) {
            this.nameSuffix = nameSuffix;
        }

        public MaritalStatus getMaritalStatus() {
            return maritalStatus;
        }
        public void setMaritalStatus(MaritalStatus maritalStatus) {
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

        public ContractorDetails getArchitect() {
            return architect;
        }
        public void setArchitect(ContractorDetails architect) {
            this.architect = architect;
        }
        public ContractorDetails getBuilder() {
            return builder;
        }
        public void setBuilder(ContractorDetails builder) {
            this.builder = builder;
        }
        public ContractorDetails getRealtor() {
            return realtor;
        }
        public void setRealtor(ContractorDetails realtor) {
            this.realtor = realtor;
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
        public ContractorDetails getPlumber() {
            return plumber;
        }
        public void setPlumber(ContractorDetails plumber) {
            this.plumber = plumber;
        }
        public List<ContractorService> getPlumbingServices() {
            return plumbingServices;
        }
        public void setPlumbingServices(List<ContractorService> plumbingServices) {
            this.plumbingServices = plumbingServices;
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
        public ContractorDetails getHvactechnician() {
            return hvactechnician;
        }
        public void setHvactechnician(ContractorDetails hvactechnician) {
            this.hvactechnician = hvactechnician;
        }
        public List<ContractorService> getHvacServices() {
            return hvacServices;
        }
        public void setHvacServices(List<ContractorService> hvacServices) {
            this.hvacServices = hvacServices;
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
        public ContractorDetails getPainter() {
            return painter;
        }
        public void setPainter(ContractorDetails painter) {
            this.painter = painter;
        }

        //:- Electrical Page
        public List<Panel> getPanels() {
            return panels;
        }
        public void setPanels(List<Panel> panels) {
            this.panels = panels;
        }
        public ContractorDetails getElectrician() {
            return electrician;
        }
        public void setElectrician(ContractorDetails electrician) {
            this.electrician = electrician;
        }
        public List<ContractorService> getElectricalServices() {
            return electricalServices;
        }
        public void setElectricalServices(List<ContractorService> electricalServices) {
            this.electricalServices = electricalServices;
        }

    //Mark:- Helper functions
    @Exclude
    public String getName() {
        return StringUtilities.getFormattedName(namePrefix, firstName, lastName, nameSuffix, maritalStatus);
    }
    @Exclude
    public void setExteriorPaintSurface(int paintId, PaintSurface surface) {
        if(paintId == -1) {
            exteriorPaintSurfaces.add(surface);
        } else {
            exteriorPaintSurfaces.set(paintId, surface);
        }
    }
    @Exclude
    public void setInteriorPaintSurface(int paintId, PaintSurface surface) {
        if(paintId == -1) {
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
    //Mark:- enums
    public enum MaritalStatus {
        MARRIED("MARRIED"),
        SINGLE("SINGLE");

        private String value;
        MaritalStatus(String value) {
            this.value = value;
        }

        public static MaritalStatus fromString(String value) {
            for(MaritalStatus status: values()) {
                if(value.toUpperCase().equals(status.value)) {
                    return status;
                }
            }
            return SINGLE;
        }
    }
}
