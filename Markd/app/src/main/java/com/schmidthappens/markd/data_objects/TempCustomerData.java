package com.schmidthappens.markd.data_objects;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.utilities.OnGetDataListener;

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
    private OnGetDataListener listener;

    public TempCustomerData(FirebaseAuthentication authentication, OnGetDataListener listener) {
        this(authentication.getCurrentUser().getUid(), listener);
    }

    public TempCustomerData(Activity activity, OnGetDataListener listener) {
        this(new FirebaseAuthentication(activity).getCurrentUser().getUid(), listener);
    }

    public TempCustomerData(String uid, OnGetDataListener listener) {
        this.uid = uid;
        this.listener = listener;
        DatabaseReference userReference = database.child(uid);
        userReference.addValueEventListener(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            customer = dataSnapshot.getValue(Customer.class);
            if(customer == null) {
                makeCustomer();
            }
            if(listener != null) {
                listener.onSuccess(dataSnapshot);
            }
            Log.d(TAG, "valueEventListener:dataChanged");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "valueEventListener:onCancelled", databaseError.toException());
            listener.onFailed(databaseError);
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
        customer.setHotWater(initialHotWater());
        customer.setBoiler(initialBoiler());
        customer.setPlumber(new ContractorDetails("SDR Plumbing & Heating Inc", "203.348.2295", "sdrplumbing.com", "06903", "plumber"));
        customer.setPlumbingServices(TempContractorServiceData.getInstance().getPlumbingServices());

        //HVAC
        customer.setAirHandler(initialAirHandler());
        customer.setCompressor(initialCompressor());
        customer.setHvactechnician(new ContractorDetails("AireServ", "203.348.2295", "aireserv.com", "06903", "hvac"));
        customer.setHvacServices(TempContractorServiceData.getInstance().getHvacServices());

        //Electrical
        //customer.setPanels(TempPanelData.getInstance().getPanels());
        //TODO: https://stackoverflow.com/questions/37368952/what-is-the-best-way-to-save-java-enums-using-firebase
        customer.setElectrician(new ContractorDetails("Conn-West Electric", "203.922.2011", "connwestelectric.com", "06478", "electrician"));
        customer.setElectricalServices(TempContractorServiceData.getInstance().getElectricalServices());

        //Painting
        customer.setInteriorPaintSurfaces(new ArrayList<PaintSurface>());
        customer.setExteriorPaintSurfaces(new ArrayList<PaintSurface>());
        customer.setPainter(new ContractorDetails("MDF Painting & Power Washing", "203.348.2295", "mdfpainting.com", "06903", "painter"));

        putCustomer(customer);
    }

    //Mark:- Home Page
    public String getName() {
        if(customer == null) {
            return "";
        }
        return customer.getName();
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
    public List<ContractorService> getPlumbingServices() {
        return getCustomer().getPlumbingServices();
    }

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
    public List<ContractorService>  getHvacServices() {
        return getCustomer().getHvacServices();
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
        customer.deleteInteriorPaintSurface(paintId);
        putCustomer(customer);
    }
    public ContractorDetails getPainter() {
        return getCustomer().getPainter();
    }

    //TODO: Delete when http calls are here
    //Remove when database is implemented
    private HotWater initialHotWater() {
        HotWater hotWater = new HotWater();
        hotWater.setManufacturer("Bosch");
        hotWater.setModel("C950 ES NG");
        hotWater.setMonth(1);
        hotWater.setDay(17);
        hotWater.setYear(2012);
        hotWater.setLifeSpan(12);
        hotWater.setUnits("years");
        return hotWater;
    }
    private Boiler initialBoiler() {
        Boiler boiler = new Boiler();
        boiler.setManufacturer("Westinghouse");
        boiler.setModel("WBRCLP140W");
        boiler.setMonth(11);
        boiler.setDay(7);
        boiler.setYear(2012);
        boiler.setLifeSpan(9);
        boiler.setUnits("years");
        return boiler;
    }
    private AirHandler initialAirHandler() {
        AirHandler airHandler = new AirHandler();
        airHandler.setManufacturer("Goodman");
        airHandler.setModel("ARUF24B14");
        airHandler.setMonth(8);
        airHandler.setDay(13);
        airHandler.setYear(2013);
        airHandler.setLifeSpan(20);
        airHandler.setUnits("years");
        return airHandler;
    }
    private Compressor initialCompressor() {
        Compressor compressor = new Compressor();
        compressor.setManufacturer("Goodman");
        compressor.setModel("GSX130361");
        compressor.setMonth(1);
        compressor.setDay(27);
        compressor.setYear(2014);
        compressor.setLifeSpan(6);
        compressor.setUnits("years");
        return compressor;
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
}
