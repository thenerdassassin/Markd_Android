package com.schmidthappens.markd.data_objects;

import android.util.Log;

import com.schmidthappens.markd.utilities.StringUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class Customer {
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
    private static final String TAG = "Customer_Bean";
    private String email;
    private String password;

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

    private Customer(String email, String password, String namePrefix, String firstName, String lastName, String nameSuffix, MaritalStatus maritalStatus, Address address, Home home, ContractorDetails architect, ContractorDetails builder, HotWater hotWater, Boiler boiler, ContractorDetails plumber, List<ContractorService> plumbingServices, AirHandler airHandler, Compressor compressor, ContractorDetails hvactechnician, List<ContractorService> hvacServices, List<Panel> panels, ContractorDetails electrician, List<ContractorService> electricalServices, List<PaintSurface> interiorPaintSurfaces, List<PaintSurface> exteriorPaintSurfaces, ContractorDetails painter) {
        this.email = email;
        this.password = password;
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
    /* TODO: implement clone functions
    Customer(Customer oldCustomer) {
        return oldCustomer;
    }*/
    public Customer(JSONObject customer) {
        //TODO: finish Customer JSON
        //Home Page
        this.namePrefix = customer.optString("namePrefix");
        this.firstName = customer.optString("firstName");
        this.lastName = customer.optString("lastName");
        this.nameSuffix = customer.optString("nameSuffix");
        this.maritalStatus = MaritalStatus.fromString(customer.optString("maritalStatus"));
        this.address = new Address(customer.optJSONObject("address"));
        //this.home = new Home(customer.getJSONObject("home"));

        //Plumbing Page
        if(customer.optJSONObject("hotWater") != null) {
            this.hotWater = new HotWater(customer.optJSONObject("hotWater"));
        }
        if(customer.optJSONObject("boiler") != null) {
            this.boiler = new Boiler(customer.optJSONObject("boiler"));
        }
        this.plumbingServices = buildServicesListFromJSONArray(customer.optJSONArray("plumbing_services"));

        //HVAC Page
        if(customer.optJSONObject("airHandler") != null) {
            this.airHandler = new AirHandler(customer.optJSONObject("airHandler"));
        }
        if(customer.optJSONObject("compressor") != null) {
            this.compressor = new Compressor(customer.optJSONObject("compressor"));
        }
        this.hvacServices = buildServicesListFromJSONArray(customer.optJSONArray("plubming_services"));

        //Electrical Page

        //Painting Page
        this.exteriorPaintSurfaces = buildPaintServicesListFromJSONArray(customer.optJSONArray("exteriorPaintSurfaces"));
        this.interiorPaintSurfaces = buildPaintServicesListFromJSONArray(customer.optJSONArray("interiorPaintSurfaces"));

        //Contractors
        if(customer.optJSONObject("architect_id") != null) {
            this.architect = new ContractorDetails(customer.optJSONObject("architect_id"));
        }
        if(customer.optJSONObject("builder_id") != null) {
            this.builder = new ContractorDetails(customer.optJSONObject("builder_id"));
        }
        if(customer.optJSONObject("plumber_id") != null) {
            this.plumber = new ContractorDetails(customer.optJSONObject("plumber_id"));
        }
        if(customer.optJSONObject("hvactechnician_id") != null) {
            this.hvactechnician = new ContractorDetails(customer.optJSONObject("hvactechnician_id"));
        }
        if(customer.optJSONObject("electrician_id") != null) {
            this.electrician = new ContractorDetails(customer.optJSONObject("electrician_id"));
        }
        if(customer.optJSONObject("painter_id") != null) {
            this.painter = new ContractorDetails(customer.optJSONObject("painter_id"));
        }
    }
    private List<ContractorService> buildServicesListFromJSONArray(JSONArray plumbingServiceList) {
        List<ContractorService> servicesToReturn = new ArrayList<>();
        if(plumbingServiceList == null) {
            return servicesToReturn;
        }
        for (int i = 0 ; i < plumbingServiceList.length(); i++) {
            JSONObject jsonService = plumbingServiceList.optJSONObject(i);
            if(jsonService != null) {
                ContractorService service = new ContractorService(
                        jsonService.optInt("month"),
                        jsonService.optInt("day"),
                        jsonService.optInt("year"),
                        jsonService.optString("contractor"),
                        jsonService.optString("comments")
                );
                servicesToReturn.add(service);
            }
        }
        return servicesToReturn;
    }
    private List<PaintSurface> buildPaintServicesListFromJSONArray(JSONArray paintSurfacesList) {
        List<PaintSurface> surfacesToReturn = new ArrayList<>();
        if(paintSurfacesList == null) {
            return surfacesToReturn;
        }
        for (int i = 0 ; i < paintSurfacesList.length(); i++) {
            JSONObject jsonPaintSurface = paintSurfacesList.optJSONObject(i);
            if(jsonPaintSurface != null) {
                PaintSurface surface = new PaintSurface(
                        jsonPaintSurface.optString("surface"),
                        jsonPaintSurface.optString("brand"),
                        jsonPaintSurface.optString("color"),
                        jsonPaintSurface.optInt("month"),
                        jsonPaintSurface.optInt("day"),
                        jsonPaintSurface.optInt("year")
                );
                surfacesToReturn.add(surface);
            }
        }
        return surfacesToReturn;
    }

    //Mark:- Home Page
    public String getName() {
        return StringUtilities.getFormattedName(namePrefix, firstName, lastName, nameSuffix, maritalStatus);
    }
    public String getAddress() {
        return this.address.toString();
    }

    //Mark:- Plumbing Page
    HotWater getHotWater() {
        if(hotWater == null) {
            return null;
        }
        return new HotWater(hotWater);
    }
    Boiler getBoiler() {
        if(boiler == null) {
            return null;
        }
        return new Boiler(boiler);
    }
    void setHotWater(HotWater hotWater) {
        this.hotWater = hotWater;
    }
    void setBoiler(Boiler boiler) {
        this.boiler = boiler;
    }
    ContractorDetails getPlumber() {
        if(plumber == null) {
            return null;
        }
        return new ContractorDetails(plumber);
    }

    //Mark:- HVAC Page
    AirHandler getAirHandler() {
        if(airHandler == null) {
            return null;
        }
        return new AirHandler(airHandler);
    }
    void setAirHandler(AirHandler airHandler) {
        this.airHandler = airHandler;
    }
    Compressor getCompressor() {
        if(compressor == null) {
            return null;
        }
        return new Compressor(compressor);
    }
    void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }
    ContractorDetails getHvacTechnician() {
        if(hvactechnician == null) {
            return null;
        }
        return new ContractorDetails(hvactechnician);
    }

    //Mark:- Painting Page
    List<PaintSurface> getExteriorPaintSurfaces() {
        if(exteriorPaintSurfaces == null) {
            return null;
        }
        List<PaintSurface> copiedList = new ArrayList<>();
        for(PaintSurface surface: exteriorPaintSurfaces) {
            copiedList.add(new PaintSurface(surface));
        }
        return copiedList;
    }
    void setExteriorPaintSurface(int paintId, PaintSurface surface) {
        if(paintId == -1) {
            exteriorPaintSurfaces.add(surface);
        } else {
            exteriorPaintSurfaces.set(paintId, surface);
        }
    }
    void setExteriorPaintSurfaces(List<PaintSurface> surfaces) {
        this.exteriorPaintSurfaces = surfaces;
    }
    void deleteExteriorPaintSurface(int paintId){
        exteriorPaintSurfaces.remove(paintId);
    }
    List<PaintSurface> getInteriorPaintSurfaces() {
        if(interiorPaintSurfaces == null) {
            return null;
        }
        List<PaintSurface> copiedList = new ArrayList<>();
        for(PaintSurface surface: interiorPaintSurfaces) {
            copiedList.add(new PaintSurface(surface));
        }
        return copiedList;
    }
    void setInteriorPaintSurface(int paintId, PaintSurface surface) {
        if(paintId == -1) {
            interiorPaintSurfaces.add(surface);
        } else {
            interiorPaintSurfaces.set(paintId, surface);
        }
    }
    void setInteriorPaintSurfaces(List<PaintSurface> surfaces) {
        this.interiorPaintSurfaces = surfaces;
    }
    void deleteInteriorPaintSurface(int paintId) {
        interiorPaintSurfaces.remove(paintId);
    }
    ContractorDetails getPainter() {
        if(painter == null) {
            return null;
        }
        return new ContractorDetails(painter);
    }
}
