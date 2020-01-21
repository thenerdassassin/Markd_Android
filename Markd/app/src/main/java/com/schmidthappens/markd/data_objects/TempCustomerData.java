package com.schmidthappens.markd.data_objects;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.file_storage.FirebaseFile;
import com.schmidthappens.markd.utilities.OnGetDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
*/

public class TempCustomerData {
    private static final String TAG = "FirebaseCustomerData";
    private static DatabaseReference database = FirebaseDatabaseInstance
            .getDatabase().getReference().child("users");
    private DatabaseReference userReference;
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
        userReference = database.child(uid);
        userReference.keepSynced(true);
        userReference.addValueEventListener(valueEventListener);
    }
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "valueEventListener:dataChanged");
            customer = dataSnapshot.getValue(Customer.class);
            if(listener != null) {
                listener.onSuccess(dataSnapshot);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "valueEventListener:onCancelled", databaseError.toException());
            listener.onFailed(databaseError);
        }
    };

    private static Customer customer;
    private Customer getCustomer() {
        return customer;
    }
    public void clearCustomerData() {
        removeListeners();
        userReference.keepSynced(false);
        customer = null;
    }
    private void putCustomer(Customer customer) {
        Log.d(TAG, "putting customer");
        database.child(uid).setValue(customer);
    }
    private void addContractorListener(final DatabaseReference reference, final OnGetDataListener contractorListener) {
        if(contractorListener != null) {
            contractorListener.onStart();
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(contractorListener != null) {
                    contractorListener.onSuccess(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(contractorListener != null) {
                    contractorListener.onFailed(databaseError);
                }
            }
        });
    }
    public void attachListener(ValueEventListener listener) {
        userReference.addListenerForSingleValueEvent(listener);
    }
    public void removeListeners() {
        userReference.removeEventListener(valueEventListener);
    }
    public String getUid() {
        return uid;
    }

    //Mark:- Home Page
    public String getName() {
        if(customer == null) {
            return "";
        }
        return customer.getName();
    }
    public String getFormattedAddress() {
        if(customer == null || customer.getAddress() == null) {
            return null;
        }
        return getStreet() + "\n" + getCity() + ", " + getState() + " " + getZipcode();
    }
    public String getRoomInformation() {
        if(customer.getHome() == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        String middot = "\u2022";

        Home home = getCustomer().getHome();
        Double bedrooms = home.getBedrooms();
        Double bathrooms = home.getBathrooms();

        if(bedrooms % 1 == 0) {
            builder.append(bedrooms.intValue());
        } else {
            builder.append(bedrooms);
        }
        if(bedrooms.equals((Double)1.0)) {
            builder.append(" bedroom ");
        } else {
            builder.append(" bedrooms ");
        }
        builder.append(middot).append(" ");
        if(bathrooms % 1 == 0) {
            builder.append(bathrooms.intValue());
        } else {
            builder.append(bathrooms);
        }
        if(bathrooms.equals((Double)1.0)) {
            builder.append(" bathroom");
        } else {
            builder.append(" bathrooms");
        }

        return builder.toString();
    }
    public String getSquareFootageString() {
        if(getCustomer().getHome() == null) {
            return null;
        } else {
            return getCustomer().getHome().getSquareFootage() + " square feet";
        }
    }
    public String getHomeImageFileName() {
        if(customer == null || customer.getHomeImageFileName() == null) {
            return null;
        } else {
            return "homes/" + uid + "/" + customer.getHomeImageFileName();
        }
    }
    public String setHomeImageFileName() {
        customer.setHomeImageFileName();
        putCustomer(customer);
        return getHomeImageFileName();
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
    public boolean getPlumber(final OnGetDataListener plumberListener) {
        String plumber = customer.getPlumberReference();
        if(plumber == null) {
            return false;
        }
        DatabaseReference plumberReference = database.child(plumber);
        addContractorListener(plumberReference, plumberListener);
        return true;
    }
    public String getPlumberReference() {
        return customer.getPlumberReference();
    }
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
    public boolean getHvacTechnician(final OnGetDataListener hvacListener) {
        String hvacTechnician = customer.getHvactechnicianReference();
        if(hvacTechnician == null) {
            return false;
        }
        DatabaseReference hvacReference = database.child(hvacTechnician);
        addContractorListener(hvacReference, hvacListener);
        return true;
    }
    public String getHvactechnicianReference() {
        return customer.getHvactechnicianReference();
    }
    public List<ContractorService> getHvacServices() {
        return getCustomer().getHvacServices();
    }

    //MarK:- Electrical
    public List<Panel> getPanels() {
        return getCustomer().getPanels();
    }
    public void updatePanel(int panelId, Panel updatedPanel){
        customer.setPanel(panelId, updatedPanel);
        putCustomer(customer);
    }
    public void removePanel(int panelId) {
        customer.deletePanel(panelId);
        putCustomer(customer);
    }
    public List<ContractorService> getElectricalServices() {
        return getCustomer().getElectricalServices();
    }
    public boolean getElectrician(final OnGetDataListener electricianListener) {
        String electrician = customer.getElectricianReference();
        if(electrician == null) {
            return false;
        }
        DatabaseReference electricianReference = database.child(electrician);
        addContractorListener(electricianReference, electricianListener);
        return true;
    }
    public String getElectricianReference() {
        return customer.getElectricianReference();
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
    public List<ContractorService> getPaintingServices() {
        return getCustomer().getPaintingServices();
    }
    public boolean getPainter(final OnGetDataListener painterListener) {
        String painter = customer.getPainterReference();
        if(painter == null) {
            return false;
        }
        DatabaseReference painterReference = database.child(painter);
        addContractorListener(painterReference, painterListener);
        return true;
    }
    public String getPainterReference() {
        return customer.getPainterReference();
    }

    //Mark:- Services
    public void addService(ContractorService service, String serviceType) {
        customer.addService(service, serviceType);
        putCustomer(customer);
    }
    public void updateService(
            int serviceId,
            String contractor,
            String date,
            String comments,
            List<FirebaseFile> files,
            String serviceType) {
        customer.updateService(serviceId, contractor, date, comments, files, serviceType);
        putCustomer(customer);
    }
    public void removeService(int serviceId, String serviceType) {
        customer.deleteService(serviceId, serviceType);
        putCustomer(customer);
    }
    public List<ContractorService> getServices(String serviceType) {
        if(customer == null) {
            return new ArrayList<>();
        } else {
            if ("Plumber".equalsIgnoreCase(serviceType)) {
                return customer.getPlumbingServices();
            } else if ("Electrician".equalsIgnoreCase(serviceType)) {
                return customer.getElectricalServices();
            } else if ("Hvac".equalsIgnoreCase(serviceType)) {
                return customer.getHvacServices();
            } else if ("painter".equalsIgnoreCase(serviceType)) {
                return customer.getPaintingServices();
            } else {
                Log.e(TAG, "No matching ServiceType");
                return new ArrayList<>();
            }
        }
    }

    //Mark:- Settings
    public String getNamePrefix() {
        return getCustomer().getNamePrefix();
    }
    public String getFirstName() {
        return getCustomer().getFirstName();
    }
    public String getLastName() {
        return getCustomer().getLastName();
    }
    public String getMaritalStatus() {
        return getCustomer().getMaritalStatus();
    }
    public void updateProfile(String namePrefix, String firstName, String lastName, String maritalStatus) {
        if(customer == null) {
            Log.e(TAG, "customer null");
            customer = new Customer();
        }
        customer.updateProfile(namePrefix, firstName, lastName, maritalStatus);
        putCustomer(customer);
    }
    public void updateContractor(final String contractorType, final String contractorReference) {
        if(contractorType.equals("Plumber")) {
            customer.setPlumber(contractorReference);
        } else if(contractorType.equals("Hvac")) {
            customer.setHvactechnician(contractorReference);
        } else if(contractorType.equals("Electrician")) {
            customer.setElectricianReference(contractorReference);
        } else if(contractorType.equals("Painter")) {
            customer.setPainter(contractorReference);
        } else {
            Log.e(TAG, "contractorType(" + contractorType + ") not found!");
        }
        putCustomer(customer);
    }

    public String getStreet() {
        return getCustomer().getAddress().getStreet();
    }
    public String getCity() {
        return getCustomer().getAddress().getCity();
    }
    public String getState() {
        return getCustomer().getAddress().getState();
    }
    public String getZipcode() {
        return getCustomer().getAddress().getZipCode();
    }
    public String getBedrooms() {
        return getCustomer().getHome().getBedrooms().toString();
    }
    public String getBathrooms() {
        return getCustomer().getHome().getBathrooms().toString();
    }
    public String getSquareFootage() {
        return getCustomer().getHome().getSquareFootage().toString();
    }
    public void updateHome(String street, String city, String state, String zipcode, Double bedrooms, Double bathrooms, Integer squareFootage) {
        if(customer == null) {
            Log.e(TAG, "customer null");
            customer = new Customer();
        }
        customer.updateHome(street, city, state, zipcode, bedrooms, bathrooms, squareFootage);
        putCustomer(customer);
    }
}
