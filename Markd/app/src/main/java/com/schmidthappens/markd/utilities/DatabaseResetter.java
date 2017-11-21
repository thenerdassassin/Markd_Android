package com.schmidthappens.markd.utilities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorDetails;
import com.schmidthappens.markd.data_objects.Customer;
import com.schmidthappens.markd.data_objects.TempContractorData;
import com.schmidthappens.markd.data_objects.TempCustomerData;

/**
 * Created by joshua.schmidtibm.com on 11/18/17.
 */

public class DatabaseResetter {
    public static void resetDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userReference = database.getReference("users");

        //Plumber
        userReference.child("defaultPlumberOne")
                .setValue(new Contractor()
                        .setType("Plumber")
                        .setContractorDetails(new ContractorDetails(
                                "SDR Plumbing & Heating",
                                "(203) 348-2295",
                                "http://www.sdrplumbing.com",
                                "06902"
                        )));
        userReference.child("defaultPlumberTwo")
                .setValue(new Contractor()
                        .setType("Plumber")
                        .setContractorDetails(new ContractorDetails(
                                "Rick's Plumbing Service, Inc",
                                "(203) 516-4542",
                                "http://www.badaraccoplumbing.com/",
                                "06460"
                        )));
        userReference.child("defaultPlumberThree")
                .setValue(new Contractor()
                        .setType("Plumber")
                        .setContractorDetails(new ContractorDetails(
                                "Badaracco Plumbing & Heating",
                                "(203) 223-1579",
                                "http://www.badaraccoplumbing.com/",
                                "06851"
                        )));
        userReference.child("defaultPlumberFour")
                .setValue(new Contractor()
                        .setType("Plumber")
                        .setContractorDetails(new ContractorDetails(
                                "Botticelli Plumbing & Heating",
                                "(203) 794-9297",
                                "http://www.botticelliplumbing.com/",
                                "06811"
                        )));

        //HVAC
        userReference.child("defaultHvacOne")
                .setValue(new Contractor()
                        .setType("Hvac")
                        .setContractorDetails(new ContractorDetails(
                                "Aireserv",
                                "(203) 348-2295",
                                "http://www.aireserv.com",
                                "06776"
                        )));
        userReference.child("defaultHvacTwo")
                .setValue(new Contractor()
                        .setType("Hvac")
                        .setContractorDetails(new ContractorDetails(
                                "Steve Basso Plumbing, Heating & A/C",
                                "(203) 335-0224",
                                "http://www.bassophac.com",
                                "06610"
                        )));
        userReference.child("defaultHvacThree")
                .setValue(new Contractor()
                        .setType("Hvac")
                        .setContractorDetails(new ContractorDetails(
                                "Aquila Heating & Cooling, LLC",
                                "(203) 334-4880",
                                "http://www.aquilaheatingandcooling.com",
                                "06824"
                        )));
        userReference.child("defaultHvacFour")
                .setValue(new Contractor()
                        .setType("Hvac")
                        .setContractorDetails(new ContractorDetails(
                                "Tyler Heating, Air Conditioning, and Refrigeration",
                                "(203) 378-4700",
                                "http://www.tylerair.com",
                                "06461"
                        )));

        //Electrician
        userReference.child("defaultElectricianOne")
                .setValue(new Contractor()
                        .setType("Electrician")
                        .setContractorDetails(new ContractorDetails(
                                "ConnWest Electric",
                                "(203) 922-2011",
                                "http://www.connwestelectric.com",
                                "27703"
                        )));
        userReference.child("defaultElectricianTwo")
                .setValue(new Contractor()
                        .setType("Electrician")
                        .setContractorDetails(new ContractorDetails(
                                "Ferrer's Electric LLC",
                                "(203) 267-0723",
                                "http://www.ferrers-electric.com",
                                "06488"
                        )));
        userReference.child("defaultElectricianThree")
                .setValue(new Contractor()
                        .setType("Electrician")
                        .setContractorDetails(new ContractorDetails(
                                "Wilton Electrical Services Inc.",
                                "(203) 834-9715",
                                "http://www.wiltonelectricalservices.com",
                                "06897"
                        )));
        userReference.child("defaultElectricianFour")
                .setValue(new Contractor()
                        .setType("Electrician")
                        .setContractorDetails(new ContractorDetails(
                                "Mazzucco Electric",
                                "(203) 257-0297",
                                "http://www.mazzuccoelectric.com",
                                "06825"
                        )));

        //Painter
        userReference.child("defaultPainterOne")
                .setValue(new Contractor()
                        .setType("Painter")
                        .setContractorDetails(new ContractorDetails(
                                "MDF Painting & Power Washing",
                                "(203) 348-2295",
                                "http://www.mdfpainting.com",
                                "06830"
                        )));
        userReference.child("defaultPainterTwo")
                .setValue(new Contractor()
                        .setType("Painter")
                        .setContractorDetails(new ContractorDetails(
                                "Wright Painting and Remodeling",
                                "(203) 221-9005",
                                "http://www.wrightpainting.info",
                                "06880"
                        )));
        userReference.child("defaultPainterThree")
                .setValue(new Contractor()
                        .setType("Painter")
                        .setContractorDetails(new ContractorDetails(
                                "Shoreline Painting & Drywall, Inc.",
                                "(203) 302-1086",
                                "http://www.shorelinepaintingct.com",
                                "06851"
                        )));
        userReference.child("defaultPainterFour")
                .setValue(new Contractor()
                        .setType("Painter")
                        .setContractorDetails(new ContractorDetails(
                                "Steve Zeisler’s Painting",
                                "(203) 254-2751",
                                "http://www.stevezeislerspainting.com/",
                                "06824"
                        )));

        //Default Customer
        userReference.child("R1EyDxbQK3Uf8ohZDNrazgM4FVc2")
                .setValue(TempCustomerData.makeCustomer());
    }
}
