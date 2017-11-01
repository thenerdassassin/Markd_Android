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
    private String maritalStatus;
    private Address address;
    private Home home;
    private ContractorDetails architect;
    private ContractorDetails builder;
    private String realtor;

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
        public String getRealtor() {
            return realtor;
        }
        public void setRealtor(String realtor) {
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
}
