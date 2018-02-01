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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
*/

public class TempCustomerData {
    private static final String TAG = "FirebaseCustomerData";
    private static DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users");
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
            public void onDataChange(DataSnapshot dataSnapshot) {
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
        if(customer == null || customer.getHome() == null) {
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
        if(customer == null) {
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
    public void updateService(int serviceId, String contractor, String comments, List<String> files, String serviceType) {
        customer.updateService(serviceId, contractor, comments, files, serviceType);
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
            if(serviceType.equalsIgnoreCase("Plumber")) {
                return customer.getPlumbingServices();
            } else if(serviceType.equalsIgnoreCase("Electrician")) {
                return customer.getElectricalServices();
            } else if(serviceType.equalsIgnoreCase("Hvac")) {
                return customer.getHvacServices();
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
    public void updateContractor(String contractorType, String contractorReference) {
        if(contractorType.equals("Plumber")) {
            if(customer.getPlumberReference() != null) {
                Log.d(TAG, "Deleting plumber:" + customer.getPlumberReference());
                TempContractorData.removeCustomerFromContractor(customer.getPlumberReference(), uid);
            }
            customer.setPlumber(contractorReference);
        } else if(contractorType.equals("Hvac")) {
            if(customer.getHvactechnicianReference() != null) {
                TempContractorData.removeCustomerFromContractor(customer.getHvactechnicianReference(), uid);
            }
            customer.setHvactechnician(contractorReference);
        } else if(contractorType.equals("Electrician")) {
            if(customer.getElectricianReference() != null) {
                TempContractorData.removeCustomerFromContractor(customer.getElectricianReference(), uid);
            }
            customer.setElectricianReference(contractorReference);
        } else if(contractorType.equals("Painter")) {
            if(customer.getPainterReference() != null) {
                TempContractorData.removeCustomerFromContractor(customer.getPainterReference(), uid);
            }
            customer.setPainter(contractorReference);
        } else if(contractorType.equals("Architect")) {
            if(customer.getArchitectReference() != null) {
                TempContractorData.removeCustomerFromContractor(customer.getArchitectReference(), uid);
            }
            customer.setArchitect(contractorReference);
        } else if(contractorType.equals("Builder")) {
            if(customer.getBuilder() != null) {
                TempContractorData.removeCustomerFromContractor(customer.getBuilder(), uid);
            }
            customer.setBuilder(contractorReference);
        } else if(contractorType.equals("Realtor")) {
            if(customer.getRealtor() != null) {
                TempContractorData.removeCustomerFromContractor(customer.getRealtor(), uid);
            }
            customer.setRealtor(contractorReference);
        } else {
            Log.e(TAG, "contractorType(" + contractorType + ") not found!");
        }
        TempContractorData.addCustomerToContractor(contractorReference, uid);
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

   //TODO: Remove with database reset
    public static Customer makeCustomer() {
        Customer newCustomer = new Customer();
        //Home Page
        newCustomer.setNamePrefix("Mr.");
        newCustomer.setFirstName("Joshua");
        newCustomer.setLastName("Schmidt");
        newCustomer.setMaritalStatus("Married");
        newCustomer.setAddress(new Address("1234 Travelers Blvd", "Darien", "CT", "06820"));
        newCustomer.setHome(new Home(5.0, 2.5, 1250));
        //newCustomer.setArchitect(new ContractorDetails("", "", "", ""));
        //newCustomer.setArchitect(new ContractorDetails("", "", "", ""));

        //Plumbing
        newCustomer.setHotWater(initialHotWater());
        newCustomer.setBoiler(initialBoiler());
        newCustomer.setPlumber("s5VWMQvH17ZJnVqxtOkqvWpufmu2");
        newCustomer.setPlumbingServices(TempContractorServiceData.getInstance().getPlumbingServices());

        //HVAC
        newCustomer.setAirHandler(initialAirHandler());
        newCustomer.setCompressor(initialCompressor());
        newCustomer.setHvactechnician("defaultHvacOne");
        newCustomer.setHvacServices(TempContractorServiceData.getInstance().getHvacServices());

        //Electrical
        newCustomer.setPanels(initialPanelList());
        newCustomer.setElectricianReference("defaultElectricianThree");
        newCustomer.setElectricalServices(TempContractorServiceData.getInstance().getElectricalServices());

        //Painting
        newCustomer.setInteriorPaintSurfaces(initialInteriorSurfaces());
        newCustomer.setExteriorPaintSurfaces(initialExteriorSurfaces());
        newCustomer.setPainter("defaultPainterOne");

        return newCustomer;
    }
    private static HotWater initialHotWater() {
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
    private static Boiler initialBoiler() {
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
    private static AirHandler initialAirHandler() {
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
    private static Compressor initialCompressor() {
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
    private static List<PaintSurface> initialExteriorSurfaces() {
        List<PaintSurface> exteriorSurfaces = new ArrayList<>();

        PaintSurface surface1 = new PaintSurface();
        surface1.setMonth(2);
        surface1.setDay(4);
        surface1.setYear(2017);
        surface1.setLocation("Siding");
        surface1.setBrand("Behr");
        surface1.setColor("Cream");
        exteriorSurfaces.add(surface1);

        PaintSurface surface2 = new PaintSurface();
        surface2.setMonth(6);
        surface2.setDay(24);
        surface2.setYear(2006);
        surface2.setLocation("Garage");
        surface2.setBrand("Behr");
        surface2.setColor("Translucent Silk");
        exteriorSurfaces.add(surface2);

        return exteriorSurfaces;
    }
    private static List<PaintSurface> initialInteriorSurfaces() {
        List<PaintSurface> interiorSurfaces = new ArrayList<>();

        PaintSurface surface1 = new PaintSurface();
        surface1.setMonth(8);
        surface1.setDay(8);
        surface1.setYear(2017);
        surface1.setLocation("Living Room");
        surface1.setBrand("Sherwin Williams");
        surface1.setColor("Light Blue");
        interiorSurfaces.add(surface1);

        PaintSurface surface2 = new PaintSurface();
        surface2.setMonth(8);
        surface2.setDay(1);
        surface2.setYear(2017);
        surface2.setLocation("Master bedroom");
        surface2.setBrand("Sherwin Williams");
        surface2.setColor("Yellow");
        interiorSurfaces.add(surface2);

        PaintSurface surface3 = new PaintSurface();
        surface3.setMonth(1);
        surface3.setDay(5);
        surface3.setYear(2014);
        surface3.setLocation("Bathroom");
        surface3.setBrand("Sherwin Williams");
        surface3.setColor("Loch Blue");
        interiorSurfaces.add(surface3);

        PaintSurface surface4 = new PaintSurface();
        surface4.setMonth(11);
        surface4.setDay(6);
        surface4.setYear(2013);
        surface4.setLocation("Dining Room");
        surface4.setBrand("Sherwin Williams");
        surface4.setColor("Grape Harvest");
        interiorSurfaces.add(surface4);

        PaintSurface surface5 = new PaintSurface();
        surface5.setMonth(11);
        surface5.setDay(6);
        surface5.setYear(2013);
        surface5.setLocation("Kitchen");
        surface5.setBrand("Sherwin Williams");
        surface5.setColor("Decor White");
        interiorSurfaces.add(surface5);

        return interiorSurfaces;
    }

    private static List<Panel> initialPanelList() {
        List<Panel> panels = new ArrayList<>();

        List<Breaker> breakerList = new LinkedList<Breaker>();
        breakerList.add(new Breaker(1, "Master Bedroom Receptacles"));
        breakerList.add(new Breaker(2, "Master Bedroom Lighting"));
        breakerList.add(new Breaker(3, "Master Bathroom GFCI"));
        breakerList.add(new Breaker(4, "Master Bathroom Floor Heat"));
        breakerList.add(new Breaker(5, "Bedroom Receptacles"));
        breakerList.add(new Breaker(6, "2nd Floor Hallway Lighting"));
        breakerList.add(new Breaker(7, "Washing Machine"));
        breakerList.add(new Breaker(8, "Dryer"));
        breakerList.add(new Breaker(9, "Hot water Heater"));
        breakerList.add(new Breaker(10, "Well pump"));
        breakerList.add(new Breaker(11, "Refrigerator"));
        breakerList.add(new Breaker(12, "Microwave"));
        breakerList.add(new Breaker(13, "Oven"));
        breakerList.add(new Breaker(14, "Kitchen Receptacles"));
        breakerList.add(new Breaker(15, "Kitchen Island Receptacles"));
        breakerList.add(new Breaker(16, "Kitchen Lighting"));
        breakerList.add(new Breaker(17, "Spot Lights"));
        breakerList.add(new Breaker(18, "Garbage Disposal"));
        breakerList.add(new Breaker(19, "Dishwasher"));
        breakerList.add(new Breaker(20, "Kitchen Hood"));
        breakerList.add(new Breaker(21, "Dining Room Receptacles"));
        breakerList.add(new Breaker(22, "Dining Room Lighting"));
        breakerList.add(new Breaker(23, "Living Room Receptacles"));
        breakerList.add(new Breaker(24, "Family Room Lighting"));
        breakerList.add(new Breaker(25, "Foyer Receptacles"));
        breakerList.add(new Breaker(26, "Foyer Lighting"));
        breakerList.add(new Breaker(27, "Furnace"));
        breakerList.add(new Breaker(28, "Air Compressor"));
        breakerList.add(new Breaker(29, "Air Handler"));
        breakerList.add(new Breaker(30, "Central Vacuum"));
        breakerList.add(new Breaker(31, "Sump Pump"));
        breakerList.add(new Breaker(32, "Basement Lighting"));
        breakerList.add(new Breaker(33, "Exterior Lighting"));
        breakerList.add(new Breaker(34, "Landscape Lighting"));
        breakerList.add(new Breaker(35, "Garage Door Receptacles"));
        panels.add(0, new Panel(true, Panel.TwoHundred, breakerList));
        panels.get(0).setPanelDescription("Attic Panel");
        panels.get(0).setInstallDate("11", "07", "16");

        List<Breaker> breakerList2 = new LinkedList<Breaker>();
        breakerList2.add(new Breaker(1, "Master Bedroom Receptacles"));
        breakerList2.add(new Breaker(2, "Master Bedroom Lighting"));
        breakerList2.add(new Breaker(3, "Master Bathroom GFCI"));
        breakerList2.add(new Breaker(4, "Master Bathroom Floor Heat"));
        breakerList2.add(new Breaker(5, "Bedroom Receptacles"));
        breakerList2.add(new Breaker(6, "2nd Floor Hallway Lighting"));
        breakerList2.add(new Breaker(7, "Washing Machine"));
        breakerList2.add(new Breaker(8, "Dryer"));
        breakerList2.add(new Breaker(9, "Hot water Heater"));
        breakerList2.add(new Breaker(10, "Well pump"));
        breakerList2.add(new Breaker(11, "Refrigerator"));
        breakerList2.add(new Breaker(12, "Microwave"));
        panels.add(1, new Panel(false, Panel.OneHundredTwentyFive, breakerList2));
        panels.get(1).setPanelDescription("Basement Panel");
        panels.get(1).setInstallDate("01", "11", "17");

        return panels;
    }
}
