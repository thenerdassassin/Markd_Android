package com.schmidthappens.markd.data_objects;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class TempCustomerData {
    private static final String TAG = "FirebaseCustomerData";
    private static DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users");
    private String uid;


    public TempCustomerData(FirebaseAuthentication authentication) {
        this(authentication.getCurrentUser().getUid());
    }

    public TempCustomerData(Activity activity) {
        this(new FirebaseAuthentication(activity).getCurrentUser().getUid());
    }

    public TempCustomerData(String uid) {
        this.uid = uid;
        DatabaseReference userReference = database.child(uid);
        userReference.addValueEventListener(valueEventListener);
        if(customer == null) {
            makeCustomer();
        }
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            customer = dataSnapshot.getValue(Customer.class);
            Log.d(TAG, "valueEventListener:dataChanged");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "valueEventListener:onCancelled", databaseError.toException());
        }
    };


    private Customer customer;
    private Customer getCustomer() {
        return customer;
    }
    private void putCustomer(Customer customer) {
        Log.d(TAG, "putting customer");
        database.child(uid).setValue(customer);
    }

    private void makeCustomer() {
        //TODO: finish dummy customer
        customer = new Customer();

        //Home Page
        customer.setNamePrefix("Mr.");
        customer.setFirstName("Joshua");
        customer.setLastName("Schmidt");
        customer.setMaritalStatus(Customer.MaritalStatus.MARRIED);
        customer.setAddress(new Address("1234 Travelers Blvd", "Darien", "CT", "06820"));
        customer.setHome(new Home(5.0, 1350.0, 2.5));
        customer.setArchitect(new ContractorDetails("", "", "", "", "architect"));
        customer.setArchitect(new ContractorDetails("", "", "", "", "builder"));

        //Plumbing
        customer.setHotWater(new HotWater());
        customer.setBoiler(new Boiler());
        customer.setPlumber(new ContractorDetails("", "", "", "", "plumber"));
        customer.setPlumbingServices(new ArrayList<ContractorService>());

        //HVAC
        customer.setAirHandler(new AirHandler());
        customer.setCompressor(new Compressor());
        customer.setHvactechnician(new ContractorDetails("", "", "", "", "hvac"));
        customer.setHvacServices(new ArrayList<ContractorService>());

        //Electrical
        customer.setPanels(new ArrayList<Panel>());
        customer.setElectrician(new ContractorDetails("", "", "", "", "electrician"));
        customer.setElectricalServices(new ArrayList<ContractorService>());

        //Painting
        customer.setInteriorPaintSurfaces(new ArrayList<PaintSurface>());
        customer.setExteriorPaintSurfaces(new ArrayList<PaintSurface>());
        customer.setPainter(new ContractorDetails("", "", "", "", "painter"));

        putCustomer(customer);
    }

    //Mark:- Home Page
    public String getName() {
        return getCustomer().getName();
    }

    //Mark:- Plumbing
    public HotWater getHotWater() {
        return getCustomer().getHotWater();
    }
    public void updateHotWater(HotWater hotWater) {
        customer.setHotWater(hotWater);
        putCustomer(customer);
    }
    public Boiler getBoiler() {
        return getCustomer().getBoiler();
    }
    public void updateBoiler(Boiler boiler) {
        customer.setBoiler(boiler);
        putCustomer(customer);
    }
    public ContractorDetails getPlumber() {return getCustomer().getPlumber();}

    //Mark:- HVAC
    public AirHandler getAirHandler() {
        return getCustomer().getAirHandler();
    }
    public void updateAirHandler(AirHandler airHandler) {
        customer.setAirHandler(airHandler);
        putCustomer(customer);
    }
    public Compressor getCompressor() {
        return getCustomer().getCompressor();
    }
    public void updateCompressor(Compressor compressor) {
        customer.setCompressor(compressor);
        putCustomer(customer);
    }
    public ContractorDetails getHvacTechnician() {
        return getCustomer().getHvactechnician();
    }

    //Mark:- Painting
    public List<PaintSurface> getExteriorSurfaces() {
        return getCustomer().getExteriorPaintSurfaces();
    }
    public void updateExteriorPaintSurface(int paintId, PaintSurface paintSurface) {
        customer.setExteriorPaintSurface(paintId, paintSurface);
        putCustomer(customer);
    }
    public void removeExteriorPaintSurface(int paintId){
        customer.deleteExteriorPaintSurface(paintId);
        putCustomer(customer);
    }
    public List<PaintSurface> getInteriorSurfaces() {
        return getCustomer().getInteriorPaintSurfaces();
    }
    public void updateInteriorPaintSurface(int paintId, PaintSurface paintSurface) {
        customer.setInteriorPaintSurface(paintId, paintSurface);
        putCustomer(customer);
    }
    public void removeInteriorPaintSurface(int paintId) {
        customer.deleteExteriorPaintSurface(paintId);
        putCustomer(customer);
    }
    public ContractorDetails getPainter() {
        return getCustomer().getPainter();
    }

    //TODO: Delete when http calls are here
    //Remove when database is implemented
    private JSONObject initialAddress() throws JSONException{
        return new JSONObject()
                .put("state", "CT")
                .put("street", "1234 Travelers Blvd")
                .put("city", "Darien")
                .put("zipCode", "06820");

    }
    private JSONObject initialHotWater() {
        JSONObject hotWaterJSON = new JSONObject();
        try {
            hotWaterJSON.put("manufacturer", "Bosch");
            hotWaterJSON.put("model", "C950 ES NG");
            hotWaterJSON.put("month", "01");
            hotWaterJSON.put("day", "17");
            hotWaterJSON.put("year", "14");
            hotWaterJSON.put("lifeSpan", "12");
            hotWaterJSON.put("units", "years");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return hotWaterJSON;
    }
    private JSONObject initialBoiler() {
        JSONObject boilerJSON = new JSONObject();
        try {
            boilerJSON.put("manufacturer", "Westinghouse");
            boilerJSON.put("model", "WBRCLP140W");
            boilerJSON.put("month", "11");
            boilerJSON.put("day", "07");
            boilerJSON.put("year", "12");
            boilerJSON.put("lifeSpan", "9");
            boilerJSON.put("units", "years");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return boilerJSON;
    }
    private JSONObject initialPlumber() {
        JSONObject plumber = new JSONObject();
        try {
            plumber.put("companyName", "SDR Plumbing & Heating Inc");
            plumber.put("telephoneNumber", "203.348.2295");
            plumber.put("websiteUrl", "sdrplumbing.com");
            plumber.put("profession", "plumber");
            plumber.put("zipCode", "06903");
        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return plumber;
    }
    private JSONObject initialAirHandler() {
        JSONObject airHandlerJSON = new JSONObject();
        try {
            airHandlerJSON.put("manufacturer", "Goodman");
            airHandlerJSON.put("model", "ARUF24B14");
            airHandlerJSON.put("month", "08");
            airHandlerJSON.put("day", "13");
            airHandlerJSON.put("year", "13");
            airHandlerJSON.put("lifeSpan", "20");
            airHandlerJSON.put("units", "years");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return airHandlerJSON;
    }
    private JSONObject initialCompressor() {
        JSONObject compressorJSON = new JSONObject();
        try {
            compressorJSON.put("manufacturer", "Goodman");
            compressorJSON.put("model", "GSX130361");
            compressorJSON.put("month", "01");
            compressorJSON.put("day", "27");
            compressorJSON.put("year", "14");
            compressorJSON.put("lifeSpan", "6");
            compressorJSON.put("units", "years");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return compressorJSON;
    }
    private JSONObject initialHvacTechnician() {
        JSONObject hvactechnician = new JSONObject();
        try {
            hvactechnician.put("companyName", "AireServ");
            hvactechnician.put("telephoneNumber", "203.348.2295");
            hvactechnician.put("websiteUrl", "aireserv.com");
            hvactechnician.put("profession", "hvac");
            hvactechnician.put("zipCode", "06903");
        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return hvactechnician;
    }
    private JSONArray initialExteriorSurfaces() {
        JSONArray exteriorSurfaces = new JSONArray();
        try {
            JSONObject surface1 = new JSONObject();
            surface1.put("month", "2");
            surface1.put("day", "4");
            surface1.put("year", "2017");
            surface1.put("surface", "Siding");
            surface1.put("brand", "Behr");
            surface1.put("color", "Cream");
            exteriorSurfaces.put(surface1);

            JSONObject surface2 = new JSONObject();
            surface2.put("month", "6");
            surface2.put("day", "24");
            surface2.put("year", "2006");
            surface2.put("surface", "Garage");
            surface2.put("brand", "Behr");
            surface2.put("color", "Translucent Silk");
            exteriorSurfaces.put(surface2);

        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return exteriorSurfaces;
    }
    private JSONArray initialInteriorSurfaces() {
        JSONArray interiorSurfaces = new JSONArray();
        try {
            JSONObject surface1 = new JSONObject();
            surface1.put("month", "8");
            surface1.put("day", "8");
            surface1.put("year", "17");
            surface1.put("surface", "Living Room");
            surface1.put("brand", "Sherwin Williams");
            surface1.put("color", "Light Blue");
            interiorSurfaces.put(surface1);

            JSONObject surface2 = new JSONObject();
            surface2.put("month", "8");
            surface2.put("day", "1");
            surface2.put("year", "17");
            surface2.put("surface", "Master bedroom");
            surface2.put("brand", "Sherwin Williams");
            surface2.put("color", "Yellow");
            interiorSurfaces.put(surface2);

            JSONObject surface3 = new JSONObject();
            surface3.put("month", "1");
            surface3.put("day", "5");
            surface3.put("year", "14");
            surface3.put("surface", "Bathroom");
            surface3.put("brand", "Sherwin Williams");
            surface3.put("color", "Loch Blue");
            interiorSurfaces.put(surface3);

            JSONObject surface4 = new JSONObject();
            surface4.put("month", "12");
            surface4.put("day", "6");
            surface4.put("year", "13");
            surface4.put("surface", "Dining Room");
            surface4.put("brand", "Sherwin Williams");
            surface4.put("color", "Grape Harvest");
            interiorSurfaces.put(surface4);

            JSONObject surface5 = new JSONObject();
            surface5.put("month", "12");
            surface5.put("day", "6");
            surface5.put("year", "13");
            surface5.put("surface", "Kitchen");
            surface5.put("brand", "Sherwin Williams");
            surface5.put("color", "Decor White");
            interiorSurfaces.put(surface5);
        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return interiorSurfaces;
    }
    private JSONObject initialPainter() {
        JSONObject painter = new JSONObject();
        try {
            painter.put("companyName", "MDF Painting & Power Washing");
            painter.put("telephoneNumber", "203.348.2295");
            painter.put("websiteUrl", "mdfpainting.com");
            painter.put("profession", "painter");
            painter.put("zipCode", "06903");
        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return painter;
    }
}
