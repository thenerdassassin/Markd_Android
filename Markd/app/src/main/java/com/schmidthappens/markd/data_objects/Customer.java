package com.schmidthappens.markd.data_objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class Customer {
    private static final String TAG = "Customer_Bean";
    private String email;
    private String password;

    //For Home Page
    private String namePrefix;
    private String firstName;
    private String lastName;
    private String nameSuffix;
    private String maritalStatus; //TODO: make enum["Married", "Single"]
    private Address address;
    private Home home;
    private Contractor architect;
    private Contractor builder;

    //For Plumbing Page
    private HotWater hotWater;
    private Boiler boiler;
    private Contractor plumber;
    private List<ContractorService> plumbingServices;

    //For HVAC Page
    private AirHandler airHandler;
    private Compressor compressor;
    private Contractor hvactechnician;
    private List<ContractorService> hvacServices;

    //For Electrical Page
    private List<Panel> panels;
    private Contractor electrician;
    private List<ContractorService> electricalServices;

    //For Painting Page
    private List<PaintSurface> interiorPaintSurfaces;
    private List<PaintSurface> exteriorPaintSurfaces;
    private Contractor painter;

    public Customer(JSONObject customer) {
        //TODO: finish Customer JSON
        //Home Page
        this.namePrefix = customer.optString("namePrefix");
        this.firstName = customer.optString("firstName");
        this.lastName = customer.optString("lastName");
        this.nameSuffix = customer.optString("nameSuffix");
        this.maritalStatus = customer.optString("maritalStatus");
        //this.address = new Address(customer.getJSONObject("address"));
        //this.home = new Home(customer.getJSONObject("home"));

        //Plumbing Page
        if(customer.optJSONObject("hotWater") != null) {
            this.hotWater = new HotWater(customer.optJSONObject("hotWater"));
        }
        if(customer.optJSONObject("boiler") != null) {
            this.boiler = new Boiler(customer.optJSONObject("boiler"));
        }
        this.plumbingServices = buildServicesListFromJSONArray(customer.optJSONArray("plubming_services"));

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
            this.architect = new Contractor(customer.optJSONObject("architect_id"));
        }
        if(customer.optJSONObject("builder_id") != null) {
            this.builder = new Contractor(customer.optJSONObject("builder_id"));
        }
        if(customer.optJSONObject("plumber_id") != null) {
            this.plumber = new Contractor(customer.optJSONObject("plumber_id"));
        }
        if(customer.optJSONObject("hvactechnician_id") != null) {
            this.hvactechnician = new Contractor(customer.optJSONObject("hvactechnician_id"));
        }
        if(customer.optJSONObject("electrician_id") != null) {
            this.electrician = new Contractor(customer.optJSONObject("electrician_id"));
        }
        if(customer.optJSONObject("painter_id") != null) {
            this.painter = new Contractor(customer.optJSONObject("painter_id"));
        }
    }
    private List<ContractorService> buildServicesListFromJSONArray(JSONArray plumbingServiceList) {
        List<ContractorService> servicesToReturn = new ArrayList<>();
        if(plumbingServiceList == null) {
            return servicesToReturn;
        }
        for (int i = 0 ; i < plumbingServiceList.length(); i++) {
            JSONObject jsonService = plumbingServiceList.optJSONObject(i);
            if(jsonService == null) {
                continue;
            }
            ContractorService service = new ContractorService(
                    jsonService.optInt("month"),
                    jsonService.optInt("day"),
                    jsonService.optInt("year"),
                    jsonService.optString("contractor"),
                    jsonService.optString("comments")
            );
            servicesToReturn.add(service);
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
            if(jsonPaintSurface == null) {
                continue;
            }
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
        return surfacesToReturn;
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
    Contractor getPlumber() {
        if(plumber == null) {
            return null;
        }
        return new Contractor(plumber);
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
    Contractor getHvacTechnician() {
        if(hvactechnician == null) {
            return null;
        }
        return new Contractor(hvactechnician);
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

    Contractor getPainter() {
        if(painter == null) {
            return null;
        }
        return new Contractor(painter);
    }
}
